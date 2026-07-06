import { Component, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Todo } from '../../models/todo.model';

@Component({
  selector: 'app-todo-item',
  imports: [DatePipe],
  templateUrl: './todo-item.html',
  styleUrl: './todo-item.scss'
})

export class TodoItem {
  todo = input.required<Todo>();
  toggle = output<number>();
  edit = output<Todo>();
  delete = output<number>();

  getPriorityLabel(priority: string): string {
    switch (priority) {
      case 'LOW': return 'Thấp';
      case 'MEDIUM': return 'Trung bình';
      case 'HIGH': return 'Cao';
      default: return priority;
    }
  }

  onToggle() {
    this.toggle.emit(this.todo().id);
  }

  onEdit() {
    this.edit.emit(this.todo());
  }

  onDelete() {
    this.delete.emit(this.todo().id);
  }
}
