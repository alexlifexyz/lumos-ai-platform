import React, { useEffect, useState } from 'react';
import { Box, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography, Chip, Dialog, DialogTitle, DialogContent, DialogActions, TextField } from '@mui/material';
import UploadFileIcon from '@mui/icons-material/UploadFile';
import axios from 'axios';

function KnowledgeBase() {
  const [documents, setDocuments] = useState([]);
  const [open, setOpen] = useState(false);
  const [file, setFile] = useState(null);
  const [namespace, setNamespace] = useState('default');

  const fetchDocuments = () => {
    axios.get('/api/v1/knowledge/documents')
      .then(response => setDocuments(response.data))
      .catch(error => console.error('Error fetching documents:', error));
  };

  useEffect(() => {
    fetchDocuments();
  }, []);

  const handleUpload = () => {
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);
    formData.append('namespace', namespace);

    axios.post('/api/v1/knowledge/upload', formData)
      .then(() => {
        setOpen(false);
        setFile(null);
        fetchDocuments(); // Refresh list
      })
      .catch(error => alert('Upload failed: ' + error.message));
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">知识库管理</Typography>
        <Button variant="contained" startIcon={<UploadFileIcon />} onClick={() => setOpen(true)}>
          Upload Document
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Filename</TableCell>
              <TableCell>Namespace</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Size</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {documents.map((doc) => (
              <TableRow key={doc.id}>
                <TableCell>{doc.id}</TableCell>
                <TableCell>{doc.filename}</TableCell>
                <TableCell>{doc.namespace}</TableCell>
                <TableCell>
                  <Chip 
                    label={doc.status} 
                    color={doc.status === 'COMPLETED' ? 'success' : doc.status === 'FAILED' ? 'error' : 'warning'} 
                    size="small" 
                  />
                </TableCell>
                <TableCell>{(doc.size / 1024).toFixed(2)} KB</TableCell>
                <TableCell>
                  <Button size="small">Details</Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle>Upload Document</DialogTitle>
        <DialogContent>
          <Box pt={2}>
            <TextField 
              label="Namespace" 
              fullWidth 
              value={namespace} 
              onChange={(e) => setNamespace(e.target.value)} 
              margin="normal"
            />
            <Button
              variant="outlined"
              component="label"
              fullWidth
              sx={{ mt: 2 }}
            >
              Choose File
              <input
                type="file"
                hidden
                onChange={(e) => setFile(e.target.files[0])}
              />
            </Button>
            {file && <Typography sx={{ mt: 1 }}>Selected: {file.name}</Typography>}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button onClick={handleUpload} variant="contained">Upload</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default KnowledgeBase;
