import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LayoutRoutingModule } from './layout-routing-module';
import { NavbarComponent } from './navbar/navbar';
import { SidebarComponent } from './sidebar/sidebar';
import { FooterComponent } from './footer/footer';
import { ShellComponent } from './shell/shell';
import { SharedModule } from '../shared/shared-module';

@NgModule({
  declarations: [NavbarComponent, SidebarComponent, FooterComponent, ShellComponent],
  imports: [CommonModule, LayoutRoutingModule, SharedModule],
  exports: [ShellComponent],
})
export class LayoutModule {}
