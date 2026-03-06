import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { finalize, switchMap, timer } from 'rxjs';

import { AdvisorService } from '../../../core/services/advisor.service';
import { AuthService } from '../../../core/services/auth.service';
import { LoadingService } from '../../../core/services/loading.service';
import { ToastService } from '../../../core/services/toast.service';
import { Advisor } from '../../../shared/models/advisor.model';
import { UserRole } from '../../../shared/models/user.model';

interface ChatMessage {
  sender: 'INVESTOR' | 'ADVISOR' | 'SYSTEM';
  text: string;
  timestamp: string;
}

@Component({
  selector: 'app-advisor-list',
  standalone: false,
  templateUrl: './advisor-list.html',
  styleUrl: './advisor-list.scss',
})
export class AdvisorListComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly advisorService = inject(AdvisorService);
  private readonly loadingService = inject(LoadingService);
  private readonly toastService = inject(ToastService);

  advisors: Advisor[] = [];
  userRole: UserRole | null = null;
  allocatedAdvisorIds: number[] = [];
  activeChatAdvisor: Advisor | null = null;
  advisorChatHistory: Record<number, ChatMessage[]> = {};
  isAdvisorTyping = false;

  readonly allocationForm = this.fb.nonNullable.group({
    advisorId: [0, [Validators.required, Validators.min(1)]],
  });

  readonly chatForm = this.fb.nonNullable.group({
    message: ['', [Validators.required, Validators.maxLength(500)]],
  });

  ngOnInit(): void {
    this.userRole = this.authService.getRole();
    this.loadAdvisors();
  }

  loadAdvisors(): void {
    this.loadingService.show();

    this.advisorService.getAdvisors().subscribe({
      next: (response) => {
        this.advisors = response.data ?? [];
      },
      error: () => {
        this.toastService.show('error', 'Advisor Error', 'Unable to fetch advisor list.');
      },
      complete: () => {
        this.loadingService.hide();
      },
    });
  }

  get isInvestor(): boolean {
    return this.userRole === 'INVESTOR';
  }

  get isInChatMode(): boolean {
    return this.isInvestor && !!this.activeChatAdvisor;
  }

  get activeChatMessages(): ChatMessage[] {
    if (!this.activeChatAdvisor) {
      return [];
    }

    return this.advisorChatHistory[this.activeChatAdvisor.id] ?? [];
  }

  allocate(): void {
    const advisorId = Number(this.allocationForm.controls.advisorId.value);
    const selectedAdvisor = this.advisors.find((advisor) => Number(advisor.id) === advisorId) ?? null;

    if (!advisorId || !selectedAdvisor) {
      this.allocationForm.markAllAsTouched();
      this.toastService.show('error', 'Advisor Error', 'Selected advisor was not found.');
      return;
    }

    if (this.isInvestor) {
      this.startChat(selectedAdvisor, false);
      if (!this.allocatedAdvisorIds.includes(advisorId)) {
        this.allocatedAdvisorIds.push(advisorId);
      }
      return;
    }

    const investorId = this.authService.getCurrentUserId();
    if (!investorId) {
      this.allocationForm.markAllAsTouched();
      this.toastService.show('error', 'Allocation Failed', 'Investor profile is unavailable for allocation.');
      return;
    }

    this.loadingService.show();

    this.advisorService.allocateAdvisor({ advisorId, investorId }).subscribe({
      next: () => {
        this.toastService.show('success', 'Advisor Allocated', 'Advisor allocation completed.');
        if (!this.allocatedAdvisorIds.includes(advisorId)) {
          this.allocatedAdvisorIds.push(advisorId);
        }
      },
      error: () => {
        this.toastService.show('error', 'Allocation Failed', 'Unable to allocate selected advisor.');
      },
      complete: () => {
        this.loadingService.hide();
      },
    });
  }

  isAllocated(advisorId: number): boolean {
    return this.allocatedAdvisorIds.includes(advisorId);
  }

  exitChat(): void {
    this.activeChatAdvisor = null;
    this.isAdvisorTyping = false;
    this.chatForm.reset({ message: '' });
  }

  sendMessage(): void {
    if (!this.activeChatAdvisor) {
      return;
    }

    const text = this.chatForm.controls.message.value.trim();
    if (!text) {
      this.chatForm.markAllAsTouched();
      return;
    }

    const advisorId = this.activeChatAdvisor.id;
    this.appendChatMessage(advisorId, {
      sender: 'INVESTOR',
      text,
      timestamp: new Date().toISOString(),
    });

    this.chatForm.reset({ message: '' });
    this.isAdvisorTyping = true;
    timer(900)
      .pipe(
        switchMap(() => this.advisorService.askChatbot(text)),
        finalize(() => {
          this.isAdvisorTyping = false;
        })
      )
      .subscribe({
        next: (answer) => {
          this.appendChatMessage(advisorId, {
            sender: 'ADVISOR',
            text: answer || "Sorry, I don't know this answer.",
            timestamp: new Date().toISOString(),
          });
        },
        error: () => {
          this.appendChatMessage(advisorId, {
            sender: 'ADVISOR',
            text: 'Unable to fetch response right now. Please try again.',
            timestamp: new Date().toISOString(),
          });
        },
      });
  }

  private startChat(advisor: Advisor, showToast = true): void {
    this.activeChatAdvisor = advisor;

    if (!this.advisorChatHistory[advisor.id]) {
      this.advisorChatHistory[advisor.id] = [];
    }

    if (this.advisorChatHistory[advisor.id].length === 0) {
      this.appendChatMessage(advisor.id, {
        sender: 'SYSTEM',
        text: `You are now connected with ${advisor.name}.`,
        timestamp: new Date().toISOString(),
      });
    }

    if (showToast) {
      this.toastService.show('success', 'Chat Ready', `You can now chat with ${advisor.name}.`);
    }
  }

  private appendChatMessage(advisorId: number, message: ChatMessage): void {
    if (!this.advisorChatHistory[advisorId]) {
      this.advisorChatHistory[advisorId] = [];
    }

    this.advisorChatHistory[advisorId].push(message);
  }
}
