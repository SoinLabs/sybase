# Sybase Node.js Bridge

## Overview

This library provides a Node.js bridge to connect to a Sybase database. It uses a Java bridge to facilitate the connection and query execution.

## Installation

```bash
npm install sybjet
```

Requirements
------------

* java 1.5+

## Usage

### Importing the Library

```javascript
const Sybase = require('sybjet');
```

### Creating a Sybase Instance

```javascript
const sybase = new Sybase({
  host: 'localhost',
  port: 5000,
  database: 'sybase',
  username: 'username',
  password: 'password',
  logTiming: true,
  pathToJavaBridge: '/path/to/JavaSybaseLink.jar',
  encoding: 'utf8',
  logs: true
});
```

### Connecting to the Database

#### Asynchronous Connection

```javascript
sybase.connect((err, data) => {
  if (err) {
    console.error(err);
    return;
  }
  console.log('Connected:', data);
});
```

#### Synchronous Connection

```javascript
const data = await sybase.connectAsync();
console.log('Connected:', data);
```

### Executing Queries

#### Asynchronous Query

```javascript
sybase.query('SELECT * FROM users', (err, result) => {
  if (err) {
    console.error(err);
    return;
  }
  console.log('Result:', result);
});
```

#### Synchronous Query

```javascript
const result = await sybase.querySync('SELECT * FROM users');
console.log('Result:', result);
```

### Disconnecting

```javascript
sybase.disconnect();
```

### Checking Connection Status

```javascript
const isConnected = sybase.isConnected();
console.log(`Is connected: ${isConnected}`);
```
