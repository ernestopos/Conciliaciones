import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NavItem } from '../../../core/models/navigation.models';

@Component({
  selector: 'app-side-menu',
  standalone: true,
  imports: [CommonModule, MatExpansionModule, MatIconModule, MatListModule, RouterLink, RouterLinkActive],
  templateUrl: './side-menu.component.html',
  styleUrl: './side-menu.component.scss'
})
export class SideMenuComponent {
  @Input() items: NavItem[] = [];
}
