import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Todo, TodoRequest, PageResponse } from '../models/todo.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TodoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getAll(
    page: number = 0,
    size: number = 10,
    completed?: boolean | null,
    keyword?: string,
    sort: string = 'createdAt,desc'
  ): Observable<PageResponse<Todo>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (completed !== null && completed !== undefined) {
      params = params.set('completed', completed.toString());
    }
    if (keyword && keyword.trim()) {
      params = params.set('keyword', keyword.trim());
    }

    return this.http.get<PageResponse<Todo>>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Todo> {
    return this.http.get<Todo>(`${this.apiUrl}/${id}`);
  }

  create(request: TodoRequest): Observable<Todo> {
    return this.http.post<Todo>(this.apiUrl, request);
  }

  update(id: number, request: TodoRequest): Observable<Todo> {
    return this.http.put<Todo>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleComplete(id: number): Observable<Todo> {
    return this.http.patch<Todo>(`${this.apiUrl}/${id}/toggle`, {});
  }
}
