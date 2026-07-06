import { test, expect } from '@playwright/test';

test.describe('Todo Application E2E Tests', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should show empty state or list of tasks', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('Danh sách công việc');
  });

  test('should validate task creation and fail on empty title', async ({ page }) => {
    await page.locator('.add-btn').click();
    await expect(page.locator('.modal-content h2')).toHaveText('Công việc mới');
    
    await page.locator('button[type="submit"]').click();
    await expect(page.locator('.error-text')).toContainText('Tiêu đề không được để trống');
    
    await page.locator('.close-btn').click();
  });

  test('should create a new todo, search for it, filter it, and complete it', async ({ page }) => {
    const todoTitle = `Task ${Date.now()}`;
    const todoDesc = 'Description of the new task';

    await page.locator('.add-btn').click();
    await page.locator('#title').fill(todoTitle);
    await page.locator('#description').fill(todoDesc);
    await page.locator('button:has-text("CAO")').click();
    await page.locator('button[type="submit"]').click();

    await expect(page.locator('.toast')).toContainText('thành công');
    await expect(page.locator('.todo-title').first()).toContainText(todoTitle);

    await page.locator('.search-box input').fill(todoTitle);
    await page.waitForTimeout(500); 
    await expect(page.locator('.todo-title')).toHaveCount(1);
    await expect(page.locator('.todo-title')).toContainText(todoTitle);

    await page.locator('.checkbox').first().click();
    await expect(page.locator('.todo-card').first()).toHaveClass(/completed/);

    await page.locator('button:has-text("Chưa hoàn thành")').click();
    await expect(page.locator('.todo-title')).toHaveCount(0);

    await page.locator('button:has-text("Đã hoàn thành")').click();
    await expect(page.locator('.todo-title').first()).toContainText(todoTitle);
  });

  test('should edit an existing todo', async ({ page }) => {
    const todoTitle = `Editable ${Date.now()}`;
    const updatedTitle = `Updated ${Date.now()}`;

    await page.locator('.add-btn').click();
    await page.locator('#title').fill(todoTitle);
    await page.locator('button[type="submit"]').click();
    await expect(page.locator('.toast')).toContainText('thành công');

    await page.locator('.search-box input').fill(todoTitle);
    await page.waitForTimeout(300);

    await page.locator('.edit-btn').first().click();
    await expect(page.locator('.modal-content h2')).toHaveText('Cập nhật công việc');
    await page.locator('#title').fill(updatedTitle);
    await page.locator('button[type="submit"]').click();

    await expect(page.locator('.toast')).toContainText('thành công');

    await page.locator('.search-box input').fill(updatedTitle);
    await page.waitForTimeout(300);
    await expect(page.locator('.todo-title').first()).toContainText(updatedTitle);
  });

  test('should delete a todo via confirmation dialog', async ({ page }) => {
    const todoTitle = `Deletable ${Date.now()}`;

    await page.locator('.add-btn').click();
    await page.locator('#title').fill(todoTitle);
    await page.locator('button[type="submit"]').click();
    await expect(page.locator('.toast')).toContainText('thành công');

    await page.locator('.search-box input').fill(todoTitle);
    await page.waitForTimeout(300);

    await page.locator('.delete-btn').first().click();
    await expect(page.locator('.dialog')).toBeVisible();
    
    await page.locator('.btn-danger').click();
    await expect(page.locator('.toast')).toContainText('thành công');
    await expect(page.locator('.todo-title')).toHaveCount(0);
  });
});
