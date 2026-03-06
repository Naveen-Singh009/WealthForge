import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse, PagedResponse } from '../../shared/models/api-response.model';
import {
  AdminPortfolioSummary,
  CreatePortfolioRequest,
  DeletePortfolioSummary,
  Holding,
  OverallPerformance,
  Portfolio,
  PortfolioAssetRequest,
  PortfolioPerformance,
  UpdatePortfolioRequest,
} from '../../shared/models/portfolio.model';

@Injectable({
  providedIn: 'root',
})
export class PortfolioService {
  private readonly apiBaseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  createPortfolio(payload: CreatePortfolioRequest): Observable<ApiResponse<Portfolio>> {
    return this.http.post<ApiResponse<Portfolio>>(`${this.apiBaseUrl}/investor/portfolios`, payload);
  }

  getInvestorPortfolios(_investorId?: number): Observable<ApiResponse<Portfolio[]>> {
    return this.http.get<ApiResponse<Portfolio[]>>(`${this.apiBaseUrl}/investor/portfolios`);
  }

  getAdminPortfolios(): Observable<ApiResponse<AdminPortfolioSummary[]>> {
    return this.http.get<ApiResponse<AdminPortfolioSummary[]>>(`${this.apiBaseUrl}/admin/portfolios`);
  }

  getAllPortfolios(): Observable<ApiResponse<AdminPortfolioSummary[]>> {
    return this.getAdminPortfolios();
  }

  getPortfolioById(portfolioId: number): Observable<ApiResponse<Portfolio>> {
    return this.http.get<ApiResponse<Portfolio>>(`${this.apiBaseUrl}/investor/portfolios/${portfolioId}`);
  }

  getPortfolioHoldings(portfolioId: number): Observable<ApiResponse<Holding[]>> {
    return this.http.get<ApiResponse<Holding[]>>(`${this.apiBaseUrl}/investor/portfolios/${portfolioId}/holdings`);
  }

  getPortfolioPerformance(portfolioId: number): Observable<ApiResponse<PortfolioPerformance>> {
    return this.http.get<ApiResponse<PortfolioPerformance>>(
      `${this.apiBaseUrl}/investor/portfolios/${portfolioId}/performance`
    );
  }

  getOverallPerformance(): Observable<ApiResponse<OverallPerformance>> {
    return this.http.get<ApiResponse<OverallPerformance>>(`${this.apiBaseUrl}/investor/portfolios/overall-performance`);
  }

  updatePortfolio(portfolioId: number, payload: UpdatePortfolioRequest): Observable<ApiResponse<Portfolio>> {
    return this.http.put<ApiResponse<Portfolio>>(`${this.apiBaseUrl}/investor/portfolios/${portfolioId}`, payload);
  }

  deletePortfolio(id: number): Observable<ApiResponse<DeletePortfolioSummary>> {
    return this.http.delete<ApiResponse<DeletePortfolioSummary>>(`${this.apiBaseUrl}/investor/portfolios/${id}`);
  }

  addAsset(portfolioId: number, payload: PortfolioAssetRequest): Observable<ApiResponse<void>> {
    return this.http.post(`${this.apiBaseUrl}/investor/buy`, {
      portfolioId,
      symbol: payload.symbol,
      quantity: payload.quantity,
    }, { responseType: 'text' }).pipe(
      map((message) => ({
        success: true,
        message: message || 'Buy order placed successfully.',
        data: undefined,
      }))
    );
  }

  getInvestorPortfoliosPaged(
    investorId: number,
    _page: number,
    _size: number,
    _sort: string
  ): Observable<PagedResponse<Portfolio>> {
    return this.getInvestorPortfolios(investorId) as unknown as Observable<PagedResponse<Portfolio>>;
  }
}
