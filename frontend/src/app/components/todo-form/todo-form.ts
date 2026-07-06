import { Component, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Todo, TodoRequest, Priority } from '../../models/todo.model';

@Component({
  selector: 'app-todo-form',
  imports: [FormsModule],
  templateUrl: './todo-form.html',
  styleUrl: './todo-form.scss'
})
export class TodoForm {
  todo = input<Todo | null>(null);
  save = output<TodoRequest>();
  cancel = output<void>();

  title = signal('');
  description = signal('');
  priority = signal<Priority>(Priority.MEDIUM);
  titleError = signal('');

  priorities = Object.values(Priority);

  ngOnInit() {
    const t = this.todo();
    if (t) {
      this.title.set(t.title);
      this.description.set(t.description || '');
      this.priority.set(t.priority);
    }
  }

  onSubmit() {
    const titleVal = this.title().trim();
    if (!titleVal) {
      this.titleError.set('Tiêu đề không được để trống');
      return;
    }
    if (titleVal.length > 255) {
      this.titleError.set('Tiêu đề không được vượt quá 255 ký tự');
      return;
    }

    const descVal = this.description().trim();
    if (descVal.length > 1000) {
      this.titleError.set('Mô tả không được vượt quá 1000 ký tự');
      return;
    }

    this.titleError.set('');
    this.save.emit({
      title: titleVal,
      description: descVal || null,
      completed: this.todo()?.completed || false,
      priority: this.priority()
    });
  }

  onCancel() {
    this.cancel.emit();
  }

  onTitleInput(value: string) {
    this.title.set(value);
    if (value.trim()) {
      this.titleError.set('');
    }
  }
}
