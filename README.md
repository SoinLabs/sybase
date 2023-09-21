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

### `connect()`

```javascript
sybase.connect((err, data) => {
  if (err) {
    console.error(err);
    return;
  }
  console.log('Connected:', data);
});
```

### `connectAsync()`

```javascript
const data = await sybase.connectAsync();
console.log('Connected:', data);
```

### Executing Queries

### `query(sqlQuery)`

```javascript
sybase.query('SELECT * FROM users', (err, result) => {
  if (err) {
    console.error(err);
    return;
  }
  console.log('Result:', result);
});
```

### `querySync(sqlQuery)`

```javascript
const result = await sybase.querySync('SELECT * FROM users');
console.log('Result:', result);
```

### `disconnect()`

```javascript
sybase.disconnect();
```

### `disconnectSync()`

The `disconnectSync` method allows you to disconnect synchronously from the database and terminates the associated Java process

```javascript
const sybase = new Sybase(...);
await sybase.disconnectSync();
```

### `isConnected()`

```javascript
const isConnected = sybase.isConnected();
console.log(`Is connected: ${isConnected}`);
```

### `transaction(queriesFunction)`

Executes a series of queries within a transaction. If any of the queries fail, the transaction will be rolled back.

#### Example

```javascript
async function main() {
  try {
    const result = await sybase.transaction(async (connection) => {
      const user = await connection.querySync('SELECT * FROM users WHERE id = 1');
      await connection.querySync(`UPDATE users SET name = 'John' WHERE id = 1`);
      return user;
    });
    console.log('Transaction successful, result:', result);
  } catch (err) {
    console.error('Transaction failed:', err);
  }
}

main();