import { useState } from 'react'
import './App.css'
import Header from './components/Header'
import Sidebar from './components/Sidebar'
import { initialCategories, initialNews, initialUsers } from './data/initialData'
import useLocalStorage from './hooks/useLocalStorage'
import CategoryPage from './pages/CategoryPage'
import DashboardPage from './pages/DashboardPage'
import LoginPage from './pages/LoginPage'
import NewsPage from './pages/NewsPage'
import SettingsPage from './pages/SettingsPage'
import UsersPage from './pages/UsersPage'

const PAGE_TITLES = {
  dashboard: 'Dashboard',
  categories: 'Category Management',
  news: 'News Management',
  users: 'User Management',
  settings: 'Settings',
}

function App() {
  const [currentUser, setCurrentUser] = useLocalStorage('funews-current-user', null)
  const [categories, setCategories] = useLocalStorage('funews-categories', initialCategories)
  const [news, setNews] = useLocalStorage('funews-news', initialNews)
  const [users, setUsers] = useLocalStorage('funews-users', initialUsers)
  const [activePage, setActivePage] = useState('dashboard')

  if (!currentUser) {
    return <LoginPage onLogin={setCurrentUser} />
  }

  const handleLogout = () => {
    setCurrentUser(null)
    setActivePage('dashboard')
  }

  const renderPage = () => {
    switch (activePage) {
      case 'categories':
        return (
          <CategoryPage
            categories={categories}
            news={news}
            setCategories={setCategories}
          />
        )
      case 'news':
        return (
          <NewsPage
            categories={categories}
            currentUser={currentUser}
            news={news}
            setNews={setNews}
          />
        )
      case 'users':
        return <UsersPage setUsers={setUsers} users={users} />
      case 'settings':
        return <SettingsPage currentUser={currentUser} />
      default:
        return (
          <DashboardPage
            categories={categories}
            news={news}
            users={users}
            onNavigate={setActivePage}
          />
        )
    }
  }

  return (
    <div className="app-shell">
      <Sidebar activePage={activePage} onNavigate={setActivePage} />
      <div className="app-main">
        <Header
          currentUser={currentUser}
          pageTitle={PAGE_TITLES[activePage]}
          onLogout={handleLogout}
        />
        <main className="page-content">{renderPage()}</main>
      </div>
    </div>
  )
}

export default App
