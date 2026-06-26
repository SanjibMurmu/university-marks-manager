import { useState } from 'react';
import axios from 'axios';
import './App.css';
import AdminControls from './AdminControls';

function App() {
  // --- STATE VARIABLES ---
  const [role, setRole] = useState('student'); // Default role
  const [username, setUsername] = useState(''); // Replaces 'rollNo' to support 'admin'
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false); 
  
  // Dashboard States
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [activeRole, setActiveRole] = useState('');
  const [data, setData] = useState(null);
  const [subjectsData, setSubjectsData] = useState(null);

  // Password Recovery States
  const [isForgotView, setIsForgotView] = useState(false);
  const [forgotStep, setForgotStep] = useState(1); // 1 = Request Code, 2 = Enter New Password
  const [resetEmail, setResetEmail] = useState('');
  const [resetToken, setResetToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [resetMessage, setResetMessage] = useState({ type: '', text: '' });

  // --- LOGIN LOGIC ---
  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const params = new URLSearchParams();
      params.append('role', role);
      params.append('username', username);
      params.append('password', password);

      // backend endpoint for login
      const response = await axios.post(`http://localhost:8080/api/students/login?${params.toString()}`);
      
      const token = response.data;
      localStorage.setItem('jwtToken', token);
      
      setIsLoggedIn(true);
      setActiveRole(role);
      setError('');
      
      // Fetch data based on who logged in
      fetchDashboardData(token, role, username);
      
    } catch (err) {
      setError('Invalid Credentials or Server Offline');
    }
  };

  // --- PASSWORD RECOVERY LOGIC ---
  const handleRequestToken = async (e) => {
    e.preventDefault();
    setResetMessage({ type: '', text: '' });
    try {
      const params = new URLSearchParams({ username, email: resetEmail });
      const response = await axios.post(`http://localhost:8080/api/students/forgot-password?${params.toString()}`);
      setResetMessage({ type: 'success', text: response.data });
      setForgotStep(2); // Move to the token entry screen
    } catch (err) {
      setResetMessage({ type: 'error', text: err.response?.data || 'Failed to send code.' });
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setResetMessage({ type: '', text: '' });
    try {
      const params = new URLSearchParams({ token: resetToken, newPassword });
      const response = await axios.post(`http://localhost:8080/api/students/reset-password?${params.toString()}`);
      
      setResetMessage({ type: 'success', text: 'Password reset successful! Redirecting to login...' });
      
      // Send them back to the login screen after 3 seconds
      setTimeout(() => {
        setIsForgotView(false);
        setForgotStep(1);
        setResetMessage({ type: '', text: '' });
        setPassword('');
      }, 3000);
    } catch (err) {
      setResetMessage({ type: 'error', text: err.response?.data || 'Invalid or expired token.' });
    }
  };

// --- FETCH DATA LOGIC ---
  const fetchDashboardData = async (token, currentRole, currentUsername) => {
    try {
      // 1. Fetch Users
      const response = await axios.get('http://localhost:8080/api/students/results', {
        headers: { Authorization: token }
      });
      
      if (currentRole === 'admin') {
        // Staff sees the whole database
        setData(response.data);
        
        // 2. NEW: Fetch Subjects exclusively for the Admin
        const subjectResponse = await axios.get('http://localhost:8080/api/students/admin/subjects', {
          headers: { Authorization: token }
        });
        setSubjectsData(subjectResponse.data);
        
      } else if (currentRole === 'teacher') {
        setData(response.data);
      } else {
        // Students only see their own row
        const myData = response.data.find(s => s.rollNo === parseInt(currentUsername));
        setData(myData ? [myData] : []);
      }
    } catch (err) {
      setError("Failed to load dashboard data.");
    }
  };

  // --- ADMIN LOGIC ---
  const handleDeleteUser = async (userToDelete) => {
    try {
      const token = localStorage.getItem('jwtToken');
      // Call the backend to delete the user
      await axios.delete(`http://localhost:8080/api/students/admin/delete-user/${userToDelete}`, {
        headers: { Authorization: token }
      });
      
      // Refresh the table data after successful deletion
      fetchDashboardData(token, activeRole, username);
      setError('');
    } catch (err) {
      setError("Failed to delete user. Make sure the backend endpoint is implemented.");
    }
  };

  // Admin Logic: Delete Subject
  const handleDeleteSubject = async (subjectName) => {
    try {
      const token = localStorage.getItem('jwtToken');
      await axios.delete(`http://localhost:8080/api/students/admin/delete-subject/${subjectName}`, {
        headers: { Authorization: token }
      });
      
      // Refresh the tables to instantly remove the deleted row from the screen
      fetchDashboardData(token, activeRole, username);
      setError('');
    } catch (err) {
      setError("Failed to delete subject. Please try again.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    setIsLoggedIn(false);
    setActiveRole('');
    setData(null);
    setUsername('');
    setPassword('');
    setError('');
  };

  return (
    <>
      {/* BACKGROUND BLOBS - Now active for the entire app */}
      <div className="background-container">
        <div className="blob blob-1"></div>
        <div className="blob blob-2"></div>
        <div className="blob blob-3"></div>
        <div className="blob blob-4"></div>
      </div>

      {/* --- UI RENDER: DASHBOARDS --- */}
      {isLoggedIn ? (
        <div className="dashboard-container" style={{ width: '800px' }}>
          <h2 className="header-title">Jadavpur University Portal</h2>
          <p className="sub-text">Information Technology Department • <b>{activeRole.toUpperCase()} VIEW</b></p>

          {error && <p className="error-text">{error}</p>}

          {/* ADMIN VIEW */}
          {activeRole === 'admin' && (
            <div>
              <AdminControls 
                  token={localStorage.getItem('jwtToken')} 
                  onRefresh={() => fetchDashboardData(localStorage.getItem('jwtToken'), activeRole, username)} 
              />

              <h3 style={{ marginTop: '30px' }}>System Database</h3>
              <table>
                <thead>
                  <tr>
                    <th>Roll No / Username</th>
                    <th>Name</th>
                    <th>Total</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {data?.map(s => (
                    <tr key={s.rollNo}>
                      <td>{s.rollNo}</td>
                      <td>{s.name}</td>
                      <td>{s.total}</td>
                      <td>
                        <button 
                          className="btn-danger" 
                          style={{ padding: '5px 10px', margin: '0' }}
                          onClick={() => handleDeleteUser(s.rollNo)}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {/* --- COURSE DIRECTORY TABLE --- */}
              <h3 style={{ marginTop: '40px' }}>Course Directory</h3>
              <table>
                <thead>
                  <tr>
                    <th>Subject Name / Code</th>
                    <th>Assigned Teacher (Username)</th>
                    <th>Action</th> {/* NEW COLUMN */}
                  </tr>
                </thead>
                <tbody>
                  {subjectsData && subjectsData.length > 0 ? (
                    subjectsData.map((sub, index) => (
                      <tr key={index}>
                        <td>{sub.subjectName}</td>
                        <td>{sub.teacherUsername}</td>
                        <td>
                          {/* NEW DELETE BUTTON */}
                          <button 
                            className="btn-danger" 
                            style={{ padding: '5px 10px', margin: '0' }}
                            onClick={() => handleDeleteSubject(sub.subjectName)}
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="3" style={{ textAlign: 'center', opacity: 0.7 }}>
                        No subjects created yet.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}

          {/* TEACHER VIEW */}
          {activeRole === 'teacher' && (
            <div>
              <h3>Grade Management</h3>
              <p><i>Select a student below to update their subject marks.</i></p>
              <table>
                <thead><tr><th>Roll No</th><th>Name</th><th>OOS</th><th>CN</th><th>Maths</th><th>Action</th></tr></thead>
                <tbody>
                  {data?.map(s => (
                    <tr key={s.rollNo}>
                      <td>{s.rollNo}</td><td>{s.name}</td><td>{s.oos}</td><td>{s.cn}</td><td>{s.maths}</td>
                      <td><button style={{padding: '5px', margin: '0'}}>Edit</button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* STUDENT VIEW */}
          {activeRole === 'student' && data && data.length > 0 && (
            <div>
              <h3>Welcome, {data[0].name}</h3>
              <table>
                <thead><tr><th>Subject</th><th>Marks</th></tr></thead>
                <tbody>
                  <tr><td>Operating Systems (OOS)</td><td>{data[0].oos}</td></tr>
                  <tr><td>Computer Networks (CN)</td><td>{data[0].cn}</td></tr>
                  <tr className="total-row"><td>Total Score</td><td>{data[0].total}</td></tr>
                </tbody>
              </table>
              <button className="btn-success">Download Report Card</button>
            </div>
          )}

          <button className="btn-danger" onClick={handleLogout} style={{ marginTop: '20px' }}>Secure Logout</button>
        </div>
      ) : (
        /* --- UI RENDER: LOGIN --- */
        <div className="login-box">
          {isForgotView ? (
              // --- FORGOT PASSWORD VIEW ---
              <>
                <h2 className="header-title">Account Recovery</h2>
                <p className="sub-text">
                  {forgotStep === 1 ? "Enter your details to receive a secure code." : "Enter the 6-digit code sent to your email."}
                </p>

                {forgotStep === 1 ? (
                  <form onSubmit={handleRequestToken}>
                    <div className="input-group">
                      <label>Username / Roll No</label>
                      <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required />
                    </div>
                    <div className="input-group">
                      <label>Registered Email</label>
                      <input type="email" value={resetEmail} onChange={(e) => setResetEmail(e.target.value)} required />
                    </div>
                    <button type="submit">Send Verification Code</button>
                  </form>
                ) : (
                  <form onSubmit={handleResetPassword}>
                    <div className="input-group">
                      <label>6-Digit Security Token</label>
                      <input type="text" value={resetToken} onChange={(e) => setResetToken(e.target.value)} required />
                    </div>
                    <div className="input-group">
                      <label>New Password</label>
                      <input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required />
                    </div>
                    <button type="submit" className="btn-success">Update Password</button>
                  </form>
                )}

                {resetMessage.text && (
                  <p className="error-text" style={{ color: resetMessage.type === 'success' ? '#155724' : '#d9534f', backgroundColor: resetMessage.type === 'success' ? '#d4edda' : 'rgba(217, 83, 79, 0.15)' }}>
                    {resetMessage.text}
                  </p>
                )}

                <button type="button" onClick={() => { setIsForgotView(false); setForgotStep(1); setResetMessage({type:'', text:''}); }} style={{ background: 'transparent', color: 'var(--primary)', boxShadow: 'none', border: '1px solid var(--primary)', marginTop: '10px' }}>
                  Back to Login
                </button>
              </>
            ) : (
              // --- STANDARD LOGIN VIEW ---
              <>
          <h2 className="header-title">Jadavpur University Portal</h2>
          <p className="sub-text">Unified Access Gateway</p>

          <form onSubmit={handleLogin}>
            <div className="input-group">
              <label>Select Role</label>
              <select value={role} onChange={(e) => setRole(e.target.value)} style={{ width: '100%', padding: '10px', marginTop: '5px' }}>
                <option value="student">Student</option>
                <option value="teacher">Subject Teacher</option>
                <option value="admin">System Administrator</option>
              </select>
            </div>

            <div className="input-group">
              <label>{role === 'student' ? 'Roll Number' : 'Username'}</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder={role === 'student' ? "e.g. 101" : "Enter username"}
                required 
              />
            </div>

      <div className="input-group">
  <label>Password</label>
  <div className="password-wrapper">
  <input
    type={showPassword ? "text" : "password"}
    value={password}
    onChange={(e) => setPassword(e.target.value)}
    placeholder="Enter your password"
    required 
  />
  <button
    type="button"
    className="password-toggle"
    onClick={() => setShowPassword(!showPassword)}
    aria-label={showPassword ? "Hide password" : "Show password"}
  >
    {showPassword ? (
      /* Eye Closed Icon (Hide) */
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24"/>
        <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68"/>
        <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61"/>
        <line x1="2" x2="22" y1="2" y2="22"/>
      </svg>
    ) : (
      /* Eye Open Icon (Show) */
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/>
        <circle cx="12" cy="12" r="3"/>
      </svg>
    )}
  </button>
</div>
</div>

            {error && <p className="error-text">{error}</p>}

            <button type="submit">Secure Login</button>
            {/* NEW: Forgot Password Text Button */}
                  <p style={{ textAlign: 'center', marginTop: '15px', fontSize: '13px' }}>
                    <a href="#" onClick={(e) => { e.preventDefault(); setIsForgotView(true); }} style={{ color: 'var(--primary)', textDecoration: 'none', fontWeight: '600' }}>
                      Forgot Password?
                    </a>
                  </p>
          </form>
        </>)}
        </div>
      )}
    </>
  );
}

export default App;