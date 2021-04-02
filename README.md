This algorithm stores data of the map type with the difference that the elements in it have a limited life span,
which can be set through the constructor.
The ConcurrentHashMap was taken as a basis, since it is thread safe.

EvictionMapSmart stores a key and value. For value uses a helper class (EvictableValueHolder) that stores:
the key, value, object lifetime and timer.

EvictableValueHolder class uses an interface (ExpirationHandler) to check the lifetime of an object and, if necessary, delete it.

Method put(), the process of replacing the key with data is implemented, thereby extending the life of the object.

Method get(), provides data by key and in case of their absence it returns null