import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import { ToastProvider } from './context/ToastContext';
import ErrorBoundary from './components/common/ErrorBoundary';
import ProtectedRoute from './components/common/ProtectedRoute';
import Navbar from './components/common/Navbar';
import Footer from './components/common/Footer';
import Login from './components/auth/Login';
import Signup from './components/auth/Signup';
import Profile from './components/auth/Profile';
import SearchRides from './components/rides/SearchRides';
import CreateRide from './components/rides/CreateRide';
import RideDetails from './components/rides/RideDetails';
import RideList from './components/rides/RideList';
import BookingList from './components/bookings/BookingList';
import ChatList from './components/messages/ChatList';
import NotificationList from './components/notifications/NotificationList';
import PointsDisplay from './components/gamification/PointsDisplay';
import Leaderboard from './components/gamification/Leaderboard';
import ReferralProgram from './components/gamification/ReferralProgram';

// Import CSS
import './styles/Toast.css';
import './styles/ErrorBoundary.css';

// NEW: Create a Layout component that conditionally shows Navbar/Footer
function Layout({ children }) {
  const location = useLocation();

  // Hide navbar and footer on auth pages
  const isAuthPage = location.pathname === '/login' ||
                     location.pathname === '/signup';

  return (
    <>
      {!isAuthPage && <Navbar />}
      {children}
      {!isAuthPage && <Footer />}
    </>
  );
}

function App() {
  return (
    <ErrorBoundary>
      <Router>
        <AuthProvider>
          <NotificationProvider>
            <ToastProvider>
              <div className="app">
                <Layout>
                <main className="main-content">
                  <Routes>
                    <Route path="/" element={<Navigate to="/search" />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/signup" element={<Signup />} />

                <Route path="/search" element={
                  <ProtectedRoute>
                    <SearchRides />
                  </ProtectedRoute>
                } />

                <Route path="/rides/create" element={
                  <ProtectedRoute>
                    <CreateRide />
                  </ProtectedRoute>
                } />

                <Route path="/rides/:id" element={
                  <ProtectedRoute>
                    <RideDetails />
                  </ProtectedRoute>
                } />

                <Route path="/my-rides" element={
                  <ProtectedRoute>
                    <RideList />
                  </ProtectedRoute>
                } />

                <Route path="/bookings" element={
                  <ProtectedRoute>
                    <BookingList />
                  </ProtectedRoute>
                } />

                <Route path="/messages" element={
                  <ProtectedRoute>
                    <ChatList />
                  </ProtectedRoute>
                } />

                <Route path="/notifications" element={
                  <ProtectedRoute>
                    <NotificationList />
                  </ProtectedRoute>
                } />

                <Route path="/profile" element={
                  <ProtectedRoute>
                    <Profile />
                  </ProtectedRoute>
                } />

                <Route path="/points" element={
                  <ProtectedRoute>
                    <PointsDisplay showHistory={true} />
                  </ProtectedRoute>
                } />

                <Route path="/leaderboard" element={
                  <ProtectedRoute>
                    <Leaderboard />
                  </ProtectedRoute>
                } />

                <Route path="/referral" element={
                  <ProtectedRoute>
                    <ReferralProgram />
                  </ProtectedRoute>
                } />
              </Routes>
            </main>
            </Layout>
          </div>
        </ToastProvider>
          </NotificationProvider>
        </AuthProvider>
      </Router>
    </ErrorBoundary>
  );
}

export default App;