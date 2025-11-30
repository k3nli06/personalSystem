export default function App() {
  const [user, setUser] = useState(null);
  function handleLogin(userData) {
    setUser(userData);
  }
  function handleLogout() {
    setUser(null);
  }
  if (!user) {
    return <Login onLogin={handleLogin} />;
  }
  if (user.role === 'hr') {
    return <HRDashboard user={user} onLogout={handleLogout} />;
  }
  return <EmployeeDashboard user={user} onLogout={handleLogout} />;
}

