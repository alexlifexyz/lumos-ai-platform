import React, { useEffect, useState } from 'react';
import { Grid, Paper, Typography, Box } from '@mui/material';
import axios from 'axios';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

function Dashboard() {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    axios.get('/api/v1/admin/audit/stats')
      .then(response => setStats(response.data))
      .catch(error => console.error('Error fetching stats:', error));
  }, []);

  if (!stats) return <Typography>Loading...</Typography>;

  return (
    <Box>
      <Typography variant="h4" gutterBottom>审计仪表盘</Typography>
      
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="h6">Total Requests</Typography>
            <Typography variant="h3" color="primary">{stats.totalRequests}</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="h6">Total Tokens</Typography>
            <Typography variant="h3" color="secondary">{stats.totalTokens}</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="h6">Avg Duration (ms)</Typography>
            <Typography variant="h3">{Math.round(stats.averageDurationMs)}</Typography>
          </Paper>
        </Grid>
      </Grid>

      {/* 模拟图表数据 (后端暂时没有提供时间序列数据) */}
      <Paper sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>Token Usage Trend (Mock Data)</Typography>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={[
            { name: 'Mon', tokens: 4000 },
            { name: 'Tue', tokens: 3000 },
            { name: 'Wed', tokens: 2000 },
            { name: 'Thu', tokens: 2780 },
            { name: 'Fri', tokens: 1890 },
            { name: 'Sat', tokens: 2390 },
            { name: 'Sun', tokens: 3490 },
          ]}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Legend />
            <Bar dataKey="tokens" fill="#8884d8" />
          </BarChart>
        </ResponsiveContainer>
      </Paper>
    </Box>
  );
}

export default Dashboard;
