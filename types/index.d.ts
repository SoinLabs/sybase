declare module "sybjet" {
  /**
   * Interface for the options to be passed to the Sybase constructor.
   */
  interface SybaseOptions {
    host: string;
    port: number;
    database: string;
    username: string;
    password: string;
    logTiming?: boolean | number;
    pathToJavaBridge?: string;
    encoding?: string;
    logs?: boolean;
  }

  /**
   * Interface for the result of a query.
   */
  interface QueryResult {
    [key: string]: any;
  }

  /**
   * Class representing a Sybase database connection.
   */
  class Sybase {
    /**
     * Creates a new Sybase instance.
     * @param options - The options for the Sybase instance.
     */
    constructor(options: SybaseOptions);

    /**
     * Connects to the Sybase database.
     * @param callback - Optional callback function to handle connection result.
     */
    connect(
      callback?: (error: Error | null, data: string | null) => void
    ): void;

    /**
     * Connects to the Sybase database asynchronously.
     * @returns A promise that resolves with a string if the connection is successful.
     */
    connectAsync(): Promise<string>;

    /**
     * Executes a SQL query.
     * @param sql - The SQL query string.
     * @param callback - Callback function to handle the query result.
     */
    query(
      sql: string,
      callback: (error: Error | null, result: QueryResult) => void
    ): void;

    /**
     * Executes a SQL query synchronously.
     * @param sql - The SQL query string.
     * @returns A promise that resolves with the result of the query.
     */
    querySync(sql: string): Promise<QueryResult>;

    /**
     * Executes a series of queries within a transaction.
     * @param queriesFunction - A function that takes the Sybase connection as an argument and returns a Promise.
     * @returns A promise that resolves with the result of the queries or rejects with an error.
     */
    transaction(
      queriesFunction: (connection: this) => Promise<any>
    ): Promise<any>;

    /**
     * Retrieves the version of the Sybase database.
     * @returns A promise that resolves with the version of the Sybase database or rejects with an error.
     */
    getVersion(): Promise<string>;

    /**
     * Disconnects from the Sybase database and kills the Java process.
     */
    disconnect(): void;

    /**
     * Disconnects synchronously from the Sybase database and kills the Java process.
     * @returns A promise that resolves when the disconnection is complete.
     */
    disconnectSync(): Promise<void>;

    /**
     * Checks if the database is connected.
     * @returns True if connected, false otherwise.
     */
    isConnected(): boolean;

    /**
     * Logs a message to the console if logs are enabled.
     * @param msg - The message to log.
     */
    log(msg: string): void;
  }

  export = Sybase;
}
