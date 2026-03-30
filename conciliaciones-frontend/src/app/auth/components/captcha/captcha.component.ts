import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-captcha',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule
  ],
  templateUrl: './captcha.component.html',
  styleUrl: './captcha.component.scss'
})
export class CaptchaComponent implements OnInit {
  @Output() validityChanged = new EventEmitter<boolean>();

  answerControl = new FormControl('', [Validators.required]);

  firstNumber = 0;
  secondNumber = 0;

  ngOnInit(): void {
    this.regenerate();
    this.answerControl.valueChanges.subscribe(() => this.emitValidity());
  }

  regenerate(): void {
    this.firstNumber = this.random(1, 9);
    this.secondNumber = this.random(1, 9);
    this.answerControl.reset('');
    this.emitValidity();
  }

  private emitValidity(): void {
    const value = Number(this.answerControl.value);
    this.validityChanged.emit(value === this.firstNumber + this.secondNumber);
  }

  private random(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }
}
