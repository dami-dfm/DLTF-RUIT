# Base image with Node
FROM node:18

# Set working directory
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the code
COPY . .

# Build TypeScript
RUN npm run build

# Expose the port your app runs on (adjust if needed)
EXPOSE 3000

# Start the app (compiled JS)
CMD ["node", "dist/app.js"]
