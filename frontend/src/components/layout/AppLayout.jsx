import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { motion } from 'framer-motion';
import {
  DashboardOutlined,
  WorkOutline,
  TaskAltOutlined,
  Groups2Outlined,
  HubOutlined,
  ManageAccountsOutlined,
  DescriptionOutlined,
  BiotechOutlined,
  Inventory2Outlined,
  ShoppingCartOutlined,
  RequestQuoteOutlined,
  NotificationsNoneOutlined,
  FactCheckOutlined,
  AssessmentOutlined,
  AutoAwesomeOutlined,
  LogoutOutlined,
  DarkModeOutlined,
  LightModeOutlined,
} from '@mui/icons-material';

import { logout } from '../../app/authSlice';
import { toggleTheme } from '../../app/themeSlice';

const NAV = [
  { to: '/', label: 'Dashboard', icon: DashboardOutlined, end: true },
  { to: '/projects', label: 'Projects', icon: WorkOutline },
  { to: '/tasks', label: 'Tasks', icon: TaskAltOutlined },
  { to: '/teams', label: 'Teams', icon: Groups2Outlined },
  { to: '/departments', label: 'Departments', icon: HubOutlined },
  { to: '/users', label: 'Users', icon: ManageAccountsOutlined },
  { to: '/documents', label: 'Documents', icon: DescriptionOutlined },
  { to: '/equipment', label: 'Equipment', icon: BiotechOutlined },
  { to: '/inventory', label: 'Inventory', icon: Inventory2Outlined },
  { to: '/procurement', label: 'Procurement', icon: ShoppingCartOutlined },
  { to: '/budget', label: 'Budget', icon: RequestQuoteOutlined },
  { to: '/reports', label: 'Reports', icon: AssessmentOutlined },
  { to: '/audit-logs', label: 'Audit Logs', icon: FactCheckOutlined },
  { to: '/ai', label: 'AI Assistant', icon: AutoAwesomeOutlined },
];

export function AppLayout() {
  const user = useSelector((s) => s.auth.user);
  const mode = useSelector((s) => s.theme.mode);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const doLogout = async () => {
    await dispatch(logout());
    navigate('/login');
  };

  return (
    <div className="flex min-h-screen bg-transparent">
      {/* --- Sidebar --- */}
      <aside className="hidden md:flex md:w-72 flex-col border-r border-ink-200/70 dark:border-ink-700/70 bg-white/70 dark:bg-ink-900/60 backdrop-blur px-6 py-8">
        <div className="flex items-center gap-3 pb-8">
          <div className="h-10 w-10 rounded-2xl bg-ink-900 dark:bg-accent grid place-items-center text-white dark:text-ink-900 font-display font-bold text-lg">
            E
          </div>
          <div>
            <div className="font-display text-lg leading-tight">ERPMS</div>
            <div className="text-[11px] tracking-widest uppercase text-ink-500 dark:text-ink-300">
              Research Platform
            </div>
          </div>
        </div>

        <nav className="flex-1 flex flex-col gap-0.5 overflow-y-auto pr-1 -mr-1">
          {NAV.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              data-testid={`nav-${item.label.toLowerCase().replace(/\s/g, '-')}`}
              className={({ isActive }) =>
                `group flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm transition-colors ${
                  isActive
                    ? 'bg-ink-900 text-white dark:bg-accent dark:text-ink-900 font-semibold shadow-card'
                    : 'text-ink-600 dark:text-ink-300 hover:bg-ink-100 dark:hover:bg-ink-800'
                }`
              }
            >
              <item.icon fontSize="small" />
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="pt-6 mt-6 border-t border-ink-200/70 dark:border-ink-700/70">
          <button
            onClick={() => dispatch(toggleTheme())}
            data-testid="theme-toggle"
            className="btn-ghost w-full justify-start"
          >
            {mode === 'dark' ? <LightModeOutlined fontSize="small" /> : <DarkModeOutlined fontSize="small" />}
            <span>{mode === 'dark' ? 'Light mode' : 'Dark mode'}</span>
          </button>
        </div>
      </aside>

      {/* --- Main --- */}
      <div className="flex-1 flex flex-col min-w-0">
        <header className="flex items-center justify-between px-6 lg:px-10 py-4 border-b border-ink-200/70 dark:border-ink-700/70 bg-white/40 dark:bg-ink-900/40 backdrop-blur">
          <div>
            <div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300">
              Welcome back
            </div>
            <div className="font-display text-xl">
              {user?.fullName || user?.email}{' '}
              <span className="chip bg-accent/20 text-accent-deep dark:text-accent-soft ml-2">
                {user?.role}
              </span>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <NavLink to="/notifications" className="btn-ghost" data-testid="header-notifications">
              <NotificationsNoneOutlined fontSize="small" />
              Alerts
            </NavLink>
            <NavLink to="/profile" className="btn-outline" data-testid="header-profile">Profile</NavLink>
            <button onClick={doLogout} className="btn-primary" data-testid="header-logout">
              <LogoutOutlined fontSize="small" />
              Sign out
            </button>
          </div>
        </header>

        <motion.main
          key={window.location.pathname}
          initial={{ opacity: 0, y: 8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.24, ease: 'easeOut' }}
          className="flex-1 min-w-0 px-6 lg:px-10 py-8"
        >
          <Outlet />
        </motion.main>
      </div>
    </div>
  );
}
