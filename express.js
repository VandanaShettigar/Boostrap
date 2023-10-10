const express = require('express');
const axios = require('axios');
const _ = require('lodash');

const app = express();
const port = 3000;

// Function to fetch blog data from the third-party API
const fetchBlogData = async () => {
  try {
    const response = await axios.get('https://intent-kit-16.hasura.app/api/rest/blogs', {
      headers: {
        'x-hasura-admin-secret': '32qR4KmXOIpsGPQKMqEJHGJS27G5s7HdSKO3gdtQd2kv5e852SiYwWNfxkZOBuQ6'
      }
    });
    return response.data;
  } catch (error) {
    throw new Error('Error fetching blog data');
  }
};

// Memoize the fetchBlogData function with a cache expiry of 5 minutes (300,000 milliseconds)
const memoizedFetchBlogData = _.memoize(fetchBlogData, null, 300000);

// Middleware for fetching and caching blog data
app.get('/api/blog-stats', async (req, res) => {
  try {
    const blogs = await memoizedFetchBlogData(); // Use the memoized function

    // Perform analysis as before
    const totalBlogs = blogs.length;
    const longestBlog = _.maxBy(blogs, (blog) => blog.title.length);
    const privacyBlogs = _.filter(blogs, (blog) => blog.title.toLowerCase().includes('privacy'));
    const numPrivacyBlogs = privacyBlogs.length;
    const uniqueBlogTitles = _.uniqBy(blogs, 'title');

    const blogStats = {
      totalBlogs,
      longestBlogTitle: longestBlog.title,
      numPrivacyBlogs,
      uniqueBlogTitles: uniqueBlogTitles.map((blog) => blog.title)
    };

    res.json(blogStats);
  } catch (error) {
    console.error('Error fetching or analyzing blog data:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Blog search endpoint
app.get('/api/blog-search', (req, res) => {
  const query = req.query.query;

  if (!query) {
    return res.status(400).json({ error: 'Query parameter is required' });
  }

  const blogs = memoizedFetchBlogData(); // Use the memoized function

  // Filter blogs based on the provided query (case-insensitive)
  const matchingBlogs = _.filter(blogs, (blog) =>
    blog.title.toLowerCase().includes(query.toLowerCase())
  );

  res.json(matchingBlogs);
});

// Start the Express server
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
