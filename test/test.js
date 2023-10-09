const expect = require("chai").expect;
const Sybase = require("../src/index.js");
const P = require("bluebird");

const sybaseCredentials = {
  host: null,
  port: 5000,
  database: null,
  username: null,
  password: null,
  pathToJavaBridge: null,
};

describe("Node Sybase Bridge", function () {
  describe("Asynchronous", () => {
    let sybase;
    let connectError;

    beforeEach((done) => {
      sybase = new Sybase(sybaseCredentials);

      sybase.connect(function (err) {
        connectError = err;
        done();
      });
    });

    after(function (done) {
      sybase.disconnect();
      done();
    });

    it("Connect", function (done) {
      expect(connectError).to.equal(null);
      expect(sybase.isConnected()).to.equal(true);
      done();
    });
  });

  describe("Synchronous", () => {
    let sybase;

    let connectError;

    before((done) => {
      sybase = new Sybase(sybaseCredentials);

      sybase
        .connectAsync()
        .then(() => {
          connectError = null;
          done();
        })
        .catch((err) => {
          connectError = err;
          done();
        });
    });

    after(() => {
      sybase.disconnect();
    });

    it("Connect", (done) => {
      expect(connectError).to.equal(null);
      expect(sybase.isConnected()).to.equal(true);
      done();
    });

    it("Get Version", async () => {
      const version = await sybase.getVersion();
      expect(version).to.be.a('string');
      expect(version.length).to.be.greaterThan(0);
      console.log('Get Version: ', version)
    });

    describe("Transaction", () => {
      it("should create a table, insert data and commit the transaction", async function () {
        expect(connectError).to.equal(null);
        expect(sybase.isConnected()).to.equal(true);

        await sybase.transaction(async (connection) => {
          await connection.querySync(
            "CREATE TABLE test_table (id INT, name VARCHAR(255))"
          );
          await connection.querySync(
            "INSERT INTO test_table (id, name) VALUES (1, 'John')"
          );
        });

        const result = await sybase.querySync(
          "SELECT * FROM test_table WHERE id = 1"
        );

        expect(result[0][0].name).to.equal("John");
      });

      it("should rollback the transaction on error", async function () {
        let errorOccurred = false;

        try {
          await sybase.transaction(async (connection) => {
            await connection.querySync(
              "INSERT INTO test_table (id, name) VALUES (2, 'Jane')"
            );

            // This should cause an error and rollback the transaction
            await connection.querySync(
              "INSERT INTO non_existent_table (id, name) VALUES (1, 'Fail')"
            );
          });
        } catch (err) {
          expect(err.message.trim()).to.equal(
            "non_existent_table not found. Specify owner.objectname or use sp_help to check whether the object exists (sp_help may produce lots of output)."
          );

          errorOccurred = true;
        }

        expect(errorOccurred).to.equal(true);

        const result = await sybase.querySync(
          "SELECT * FROM test_table WHERE id = 2"
        );

        expect(result[0].length).to.equal(0);
      });

      it('should get error of already created a table named "test_table"', async function () {
        try {
          await sybase.transaction(async (connection) => {
            await connection.querySync(
              "CREATE TABLE test_table (id INT, name VARCHAR(255))"
            );
            await connection.querySync(
              "INSERT INTO test_table (id, name) VALUES (1, 'John')"
            );
          });

          await sybase.querySync("SELECT * FROM test_table WHERE id = 1");
        } catch (err) {
          expect(err.message.trim()).to.equal(
            "There is already an object named 'test_table' in the database."
          );
        }
      });

      it('should clean up by dropping a table named "test_table"', async function () {
        try {
          await sybase.querySync("DROP TABLE test_table");
        } catch (err) {
          if (
            !err.message.includes("not found") &&
            !err.message.includes("does not exist")
          ) {
            throw err;
          }
        }
      });
    });
  });
});
