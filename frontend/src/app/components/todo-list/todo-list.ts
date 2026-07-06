import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { TodoService } from '../../services/todo.service';
import { Todo, TodoRequest, PageResponse } from '../../models/todo.model';
import { TodoItem } from '../todo-item/todo-item';
import { TodoForm } from '../todo-form/todo-form';
import { ConfirmDialog } from '../confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-todo-list',
  imports: [TodoItem, TodoForm, ConfirmDialog],
  templateUrl: './todo-list.html',
  styleUrl: './todo-list.scss'
})
export class TodoList implements OnInit {
  private readonly todoService = inject(TodoService);

  todos = signal<Todo[]>([]);
  totalElements = signal(0);
  totalPages = signal(0);
  currentPage = signal(0);
  pageSize = signal(10);
  isFirst = signal(true);
  isLast = signal(true);

  loading = signal(false);
  searchKeyword = signal('');
  activeFilter = signal<boolean | null>(null);
  sortField = signal('createdAt,desc');

  showForm = signal(false);
  editingTodo = signal<Todo | null>(null);
  deleteTargetId = signal<number | null>(null);

  toast = signal<{ message: string; type: 'success' | 'error' } | null>(null);

  filterCounts = computed(() => {
    const all = this.totalElements();
    return { all };
  });

  ngOnInit() {
    this.loadTodos();
  }

  loadTodos() {
    this.loading.set(true);
    this.todoService.getAll(
      this.currentPage(),
      this.pageSize(),
      this.activeFilter(),
      this.searchKeyword(),
      this.sortField()
    ).subscribe({
      next: (page: PageResponse<Todo>) => {
        this.todos.set(page.content);
        this.totalElements.set(page.totalElements);
        this.totalPages.set(page.totalPages);
        this.isFirst.set(page.first);
        this.isLast.set(page.last);
        this.loading.set(false);
      },
      error: () => {
        this.showToast('Không thể tải danh sách công việc', 'error');
        this.loading.set(false);
      }
    });
  }

  onSearch(value: string) {
    this.searchKeyword.set(value);
    this.currentPage.set(0);
    this.loadTodos();
  }

  onFilterChange(filter: boolean | null) {
    this.activeFilter.set(filter);
    this.currentPage.set(0);
    this.loadTodos();
  }

  onSortChange(sort: string) {
    this.sortField.set(sort);
    this.currentPage.set(0);
    this.loadTodos();
  }

  goToPage(page: number) {
    this.currentPage.set(page);
    this.loadTodos();
  }

  openCreateForm() {
    this.editingTodo.set(null);
    this.showForm.set(true);
  }

  openEditForm(todo: Todo) {
    this.editingTodo.set(todo);
    this.showForm.set(true);
  }

  closeForm() {
    this.showForm.set(false);
    this.editingTodo.set(null);
  }

  onSave(request: TodoRequest) {
    const editing = this.editingTodo();
    if (editing) {
      this.todoService.update(editing.id, request).subscribe({
        next: () => {
          this.showToast('Cập nhật công việc thành công', 'success');
          this.closeForm();
          this.loadTodos();
        },
        error: () => this.showToast('Không thể cập nhật công việc', 'error')
      });
    } else {
      this.todoService.create(request).subscribe({
        next: () => {
          this.showToast('Tạo công việc thành công', 'success');
          this.closeForm();
          this.currentPage.set(0);
          this.loadTodos();
        },
        error: () => this.showToast('Không thể tạo công việc', 'error')
      });
    }
  }

  onToggle(id: number) {
    this.todoService.toggleComplete(id).subscribe({
      next: (updated) => {
        this.todos.update(todos =>
          todos.map(t => t.id === id ? updated : t)
        );
      },
      error: () => this.showToast('Không thể cập nhật công việc', 'error')
    });
  }

  confirmDelete(id: number) {
    this.deleteTargetId.set(id);
  }

  cancelDelete() {
    this.deleteTargetId.set(null);
  }

  executeDelete() {
    const id = this.deleteTargetId();
    if (id === null) return;

    this.todoService.delete(id).subscribe({
      next: () => {
        this.showToast('Xóa công việc thành công', 'success');
        this.deleteTargetId.set(null);
        this.loadTodos();
      },
      error: () => {
        this.showToast('Không thể xóa công việc', 'error');
        this.deleteTargetId.set(null);
      }
    });
  }

  private showToast(message: string, type: 'success' | 'error') {
    this.toast.set({ message, type });
    setTimeout(() => this.toast.set(null), 3000);
  }

  getPageNumbers(): number[] {
    const total = this.totalPages();
    const current = this.currentPage();
    const pages: number[] = [];

    let start = Math.max(0, current - 2);
    let end = Math.min(total - 1, current + 2);

    if (end - start < 4) {
      if (start === 0) {
        end = Math.min(total - 1, 4);
      } else {
        start = Math.max(0, total - 5);
      }
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    return pages;
  }
}
