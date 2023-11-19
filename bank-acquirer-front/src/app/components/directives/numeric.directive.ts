import { Directive, HostListener } from '@angular/core';

@Directive({
  selector: '[appNumericOnly]'
})
export class NumericDirective {
  @HostListener('input', ['$event'])
  onInput(event: any): void {
    const input = event.target;
    input.value = input.value.replace(/[^0-9]/g, '');
  }
}
