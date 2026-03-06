import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'emptyState',
  standalone: false,
})
export class EmptyStatePipe implements PipeTransform {
  transform<T>(value: T | null | undefined, fallback = 'N/A'): T | string {
    if (value === null || value === undefined) {
      return fallback;
    }

    if (typeof value === 'string' && value.trim().length === 0) {
      return fallback;
    }

    return value;
  }
}
