Stage 1

Register instances

It should be possible to register an instance, identified by an address

Each address should be unique, it should not be possible to register the same address more than once

Load Balancer should accept up to 10 addresses


Stage 2

Random invocation

Develop an algorithm that,

- when invoking the Load Balancer 's get() method multiple times,
- should return one backend-instance choosing between the registered ones randomly.
-
-
- Serivce1, service2, service3....
-
- Stage 3