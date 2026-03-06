import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../shared/models/api-response.model';
import { Advisor, AllocateAdvisorRequest } from '../../shared/models/advisor.model';

@Injectable({
  providedIn: 'root',
})
export class AdvisorService {
  private readonly apiBaseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  getAdvisors(): Observable<ApiResponse<Advisor[]>> {
    return this.http.get<Advisor[]>(`${this.apiBaseUrl}/advisor/list/all`).pipe(
      map((advisors) => ({
        success: true,
        message: 'Advisors loaded',
        data: advisors ?? [],
      }))
    );
  }

  allocateAdvisor(payload: AllocateAdvisorRequest): Observable<ApiResponse<void>> {
    return this.http.post<unknown>(`${this.apiBaseUrl}/advisor/assign`, payload).pipe(
      map(() => ({
        success: true,
        message: 'Advisor allocated',
        data: undefined,
      }))
    );
  }

  askChatbot(question: string): Observable<string> {
    return this.http.get(`${this.apiBaseUrl}/advisor/chatbot/ask`, {
      params: { question },
      responseType: 'text',
    });
  }
}
