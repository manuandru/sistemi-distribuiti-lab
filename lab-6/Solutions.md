# Solutions

## Issue

### Issue 1
In `it.unibo.ds.ws.users.UserController#getAllUserNames` OpenApi specification, `limit` parameter is set to `10`as default. According to `it.unibo.ds.lab.ws.AbstractTestAuthenticator#testGetAll`, the test try to get all users (13), but it can receive only 10 of them.

> I changed the limit to 20 in implementation
