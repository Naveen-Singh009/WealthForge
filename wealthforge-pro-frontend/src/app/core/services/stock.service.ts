import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../shared/models/api-response.model';
import { CreateStockRequest, Stock, UpdateStockRequest } from '../../shared/models/stock.model';

@Injectable({
  providedIn: 'root',
})
export class StockService {
  private readonly apiBaseUrl = environment.apiBaseUrl;
  private readonly stocksEndpoint = `${this.apiBaseUrl}/admin/stocks`;

  constructor(private readonly http: HttpClient) {}

  getAllStocks(): Observable<ApiResponse<Stock[]>> {
    return this.http.get<Stock[]>(this.stocksEndpoint).pipe(
      map((stocks) => ({
        success: true,
        message: 'Stocks loaded',
        data: (stocks ?? []).map((stock) => ({
          ...stock,
          name: stock.name ?? stock.symbol,
          sector: stock.sector ?? 'N/A',
          availableQuantity: stock.availableQuantity ?? 0,
        })),
      }))
    );
  }

  createStock(payload: CreateStockRequest): Observable<ApiResponse<string>> {
    return this.http.post<string>(this.stocksEndpoint, payload).pipe(
      map((message) => ({
        success: true,
        message: typeof message === 'string' ? message : 'Stock created successfully.',
        data: typeof message === 'string' ? message : 'Stock created successfully.',
      }))
    );
  }

  updateStock(id: number, payload: UpdateStockRequest): Observable<ApiResponse<Stock>> {
    return this.http.put<Stock>(`${this.stocksEndpoint}/${id}`, payload).pipe(
      map((stock) => ({
        success: true,
        message: 'Stock updated successfully.',
        data: {
          ...stock,
          name: stock.name ?? stock.symbol,
          sector: stock.sector ?? 'N/A',
          availableQuantity: stock.availableQuantity ?? 0,
        },
      }))
    );
  }

  searchStocks(name: string): Observable<ApiResponse<Stock[]>> {
    const q = name.trim().toLowerCase();
    return this.getAllStocks().pipe(
      map((response) => ({
        ...response,
        data: (response.data ?? []).filter(
          (stock) =>
            stock.symbol.toLowerCase().includes(q) ||
            (stock.name ?? '').toLowerCase().includes(q) ||
            (stock.sector ?? '').toLowerCase().includes(q)
        ),
      }))
    );
  }

  getStockById(id: number): Observable<ApiResponse<Stock | null>> {
    return this.getAllStocks().pipe(
      map((response) => ({
        success: response.success,
        message: response.message,
        data: (response.data ?? []).find((stock) => stock.id === id) ?? null,
      }))
    );
  }
}
