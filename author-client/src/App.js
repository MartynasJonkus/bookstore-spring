import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BrowserRouter as Router, Route, Link, Routes, useParams } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';

const API_URL = 'http://localhost:8081/api/authors';

function AuthorList() {
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAuthors = async () => {
      try {
        const response = await axios.get(API_URL);
        setAuthors(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching authors:', error);
        setLoading(false);
      }
    };
    fetchAuthors();
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this author?')) {
      try {
        await axios.delete(`${API_URL}/${id}`);
        setAuthors(authors.filter(author => author.id !== id));
      } catch (error) {
        console.error('Error deleting author:', error);
      }
    }
  };

  if (loading) return <div className="container mt-3">Loading...</div>;

  return (
    <div className="container mt-3">
      <h2>Authors</h2>
      <Link to="/add" className="btn btn-primary mb-3">Add New Author</Link>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {authors.map(author => (
            <tr key={author.id}>
              <td>{author.id}</td>
              <td>{author.firstName}</td>
              <td>{author.lastName}</td>
              <td>
                <Link to={`/edit/${author.id}`} className="btn btn-sm btn-warning me-2">
                  Edit
                </Link>
                <button 
                  onClick={() => handleDelete(author.id)}
                  className="btn btn-sm btn-danger"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function AuthorForm({ existingAuthor }) {
  const [formData, setFormData] = useState({
    firstName: existingAuthor?.firstName || '',
    lastName: existingAuthor?.lastName || ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (existingAuthor) {
        await axios.put(`${API_URL}/${existingAuthor.id}`, formData);
      } else {
        await axios.post(API_URL, formData);
      }
      window.location = '/';
    } catch (error) {
      console.error('Error saving author:', error);
    }
  };

  return (
    <div className="container mt-3">
      <h2>{existingAuthor ? 'Edit Author' : 'Add New Author'}</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">First Name</label>
          <input
            type="text"
            className="form-control"
            value={formData.firstName}
            onChange={e => setFormData({...formData, firstName: e.target.value})}
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Last Name</label>
          <input
            type="text"
            className="form-control"
            value={formData.lastName}
            onChange={e => setFormData({...formData, lastName: e.target.value})}
            required
          />
        </div>
        <button type="submit" className="btn btn-primary">
          {existingAuthor ? 'Update' : 'Create'}
        </button>
        <Link to="/" className="btn btn-secondary ms-2">Cancel</Link>
      </form>
    </div>
  );
}

function App() {
  return (
    <Router>
      <nav className="navbar navbar-expand navbar-dark bg-dark">
        <div className="container">
          <Link to="/" className="navbar-brand">Bookstore</Link>
        </div>
      </nav>
      
      <Routes>
        <Route path="/" element={<AuthorList />} />
        <Route path="/add" element={<AuthorForm />} />
        <Route path="/edit/:id" element={<EditAuthorWrapper />} />
      </Routes>
    </Router>
  );
}

function EditAuthorWrapper() {
  const [author, setAuthor] = useState(null);
  const [loading, setLoading] = useState(true);
  const { id } = useParams();

  useEffect(() => {
    const fetchAuthor = async () => {
      try {
        const response = await axios.get(`${API_URL}/${id}`);
        setAuthor(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching author:', error);
        setLoading(false);
      }
    };
    fetchAuthor();
  }, [id]);

  if (loading) return <div className="container mt-3">Loading...</div>;
  if (!author) return <div className="container mt-3">Author not found</div>;

  return <AuthorForm existingAuthor={author} />;
}

export default App;