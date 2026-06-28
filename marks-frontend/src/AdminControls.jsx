import React, { useState } from 'react';

export default function AdminControls({ token, onRefresh }) {
    // User Form State
    const [userForm, setUserForm] = useState({ username: '', password: '', role: 'student', fullName: '',email: '' });
    const [userMessage, setUserMessage] = useState({ type: '', text: '' });

    // Subject Form State
    const [subjectForm, setSubjectForm] = useState({ subjectName: '', teacherUsername: '' });
    const [subjectMessage, setSubjectMessage] = useState({ type: '', text: '' });

    // Handle User Submission
    const handleAddUser = async (e) => {
        e.preventDefault();
        setUserMessage({ type: '', text: '' });

        try {
            const params = new URLSearchParams({
                username: userForm.username,
                password: userForm.password,
                role: userForm.role,
                fullName: userForm.fullName,
                email: userForm.email
            });

            const response = await fetch(`https://university-marks-manager.onrender.com/api/students/admin/add-user?${params.toString()}`, {
                method: 'POST',
                headers: { 'Authorization': token }
            });

            const data = await response.text();

            if (response.ok) {
                setUserMessage({ type: 'success', text: data });
                setUserForm({ username: '', password: '', role: 'student', fullName: '' });
                if (onRefresh) onRefresh(); // Trigger table reload if applicable
            } else {
                setUserMessage({ type: 'error', text: data });
            }
        } catch (err) {
            setUserMessage({ type: 'error', text: 'Failed to connect to the backend server.' });
        }
    };

    // Handle Subject Submission
    const handleAddSubject = async (e) => {
        e.preventDefault();
        setSubjectMessage({ type: '', text: '' });

        try {
            const params = new URLSearchParams({
                subjectName: subjectForm.subjectName,
                teacherUsername: subjectForm.teacherUsername
            });

            const response = await fetch(`https://university-marks-manager.onrender.com/api/students/admin/add-subject?${params.toString()}`, {
                method: 'POST',
                headers: { 'Authorization': token }
            });

            const data = await response.text();

            if (response.ok) {
                setSubjectMessage({ type: 'success', text: data });
                setSubjectForm({ subjectName: '', teacherUsername: '' });
            } else {
                setSubjectMessage({ type: 'error', text: data });
            }
        } catch (err) {
            setSubjectMessage({ type: 'error', text: 'Failed to connect to the backend server.' });
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
            <h2 style={{ borderBottom: '2px solid #ccc', paddingBottom: '10px' }}>Admin Command Center</h2>
            
            {/* Form 1: Add Users */}
            <div style={{ marginBottom: '40px', padding: '20px', border: '1px solid #ddd', borderRadius: '8px' }}>
                <h3>Register New User (Student / Teacher)</h3>
                <form onSubmit={handleAddUser} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    <input 
                        type="text" placeholder="Full Name (e.g., Samir Murmu)" required
                        value={userForm.fullName} onChange={e => setUserForm({...userForm, fullName: e.target.value})}
                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                    <input 
                        type="text" placeholder="Username / Roll No (e.g., 104 or T-OOS)" required
                        value={userForm.username} onChange={e => setUserForm({...userForm, username: e.target.value})}
                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                    <input 
                        type="email" placeholder="Registered Email (e.g., user@jadavpuruniversity.in)" required
                        value={userForm.email} onChange={e => setUserForm({...userForm, email: e.target.value})}
                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                    <input 
                        type="password" placeholder="Password" required
                        value={userForm.password} onChange={e => setUserForm({...userForm, password: e.target.value})}
                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                    <select 
                        value={userForm.role} onChange={e => setUserForm({...userForm, role: e.target.value})}
                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    >
                        <option value="student">Student</option>
                        <option value="teacher">Subject Teacher</option>
                    </select>
                    <button type="submit" style={{ padding: '10px', background: '#007bff', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                        Register Account
                    </button>
                </form>
                {userMessage.text && (
                    <div style={{ marginTop: '10px', padding: '10px', borderRadius: '4px', backgroundColor: userMessage.type === 'success' ? '#d4edda' : '#f8d7da', color: userMessage.type === 'success' ? '#155724' : '#721c24' }}>
                        {userMessage.text}
                    </div>
                )}
            </div>

            {/* Form 2: Add Subjects */}
            <div style={{ padding: '20px', border: '1px solid #ddd', borderRadius: '8px' }}>
                <h3>Create Course Subject</h3>
                <form onSubmit={handleAddSubject} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    <input 
                        type="text" placeholder="Subject Code / Name (e.g., Computer Networks)" required
                        value={subjectForm.subjectName} onChange={e => setSubjectForm({...subjectForm, subjectName: e.target.value})}
                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                    <input 
                        type="text" placeholder="Assigned Teacher Username" required
                        value={subjectForm.teacherUsername} onChange={e => setSubjectForm({...subjectForm, teacherUsername: e.target.value})}
                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                    <button type="submit" style={{ padding: '10px', background: '#28a745', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                        Link & Save Subject
                    </button>
                </form>
                {subjectMessage.text && (
                    <div style={{ marginTop: '10px', padding: '10px', borderRadius: '4px', backgroundColor: subjectMessage.type === 'success' ? '#d4edda' : '#f8d7da', color: subjectMessage.type === 'success' ? '#155724' : '#721c24' }}>
                        {subjectMessage.text}
                    </div>
                )}
            </div>
        </div>
    );
}
