import { Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import ForgotPasswordPage from '../pages/auth/ForgotPasswordPage';
import ResetPasswordPage from '../pages/auth/ResetPasswordPage';

import { AppLayout } from '../components/layout/AppLayout';
import DashboardPage from '../pages/DashboardPage';
import ProjectsPage from '../pages/projects/ProjectsPage';
import ProjectDetailPage from '../pages/projects/ProjectDetailPage';
import TasksBoardPage from '../pages/tasks/TasksBoardPage';
import TeamsPage from '../pages/teams/TeamsPage';
import DepartmentsPage from '../pages/departments/DepartmentsPage';
import UsersPage from '../pages/users/UsersPage';
import ProfilePage from '../pages/users/ProfilePage';
import DocumentsPage from '../pages/documents/DocumentsPage';
import EquipmentPage from '../pages/equipment/EquipmentPage';
import InventoryPage from '../pages/inventory/InventoryPage';
import ProcurementPage from '../pages/procurement/ProcurementPage';
import BudgetPage from '../pages/budget/BudgetPage';
import NotificationsPage from '../pages/notifications/NotificationsPage';
import AuditLogsPage from '../pages/audit/AuditLogsPage';
import ReportsPage from '../pages/reports/ReportsPage';
import AiAssistantPage from '../pages/ai/AiAssistantPage';

function RequireAuth({ children }) {
  const user = useSelector((s) => s.auth.user);
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      <Route path="/reset-password" element={<ResetPasswordPage />} />

      <Route
        path="/"
        element={
          <RequireAuth>
            <AppLayout />
          </RequireAuth>
        }
      >
        <Route index element={<DashboardPage />} />
        <Route path="projects" element={<ProjectsPage />} />
        <Route path="projects/:id" element={<ProjectDetailPage />} />
        <Route path="tasks" element={<TasksBoardPage />} />
        <Route path="teams" element={<TeamsPage />} />
        <Route path="departments" element={<DepartmentsPage />} />
        <Route path="users" element={<UsersPage />} />
        <Route path="profile" element={<ProfilePage />} />
        <Route path="documents" element={<DocumentsPage />} />
        <Route path="equipment" element={<EquipmentPage />} />
        <Route path="inventory" element={<InventoryPage />} />
        <Route path="procurement" element={<ProcurementPage />} />
        <Route path="budget" element={<BudgetPage />} />
        <Route path="notifications" element={<NotificationsPage />} />
        <Route path="audit-logs" element={<AuditLogsPage />} />
        <Route path="reports" element={<ReportsPage />} />
        <Route path="ai" element={<AiAssistantPage />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
