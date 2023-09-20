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
  describe("Synchronous", () => {
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

  describe("Asynchronous", () => {
    let sybase;

    let connectError;

    beforeEach((done) => {
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
  });
});
