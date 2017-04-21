This demonstrates a convoluted way to get tests to hang (I waited a couple minutes, anyway). The dependencies are set up
such that cassandra-all pulls in netty-all 4.0.x, and apparently the ByteBuf class gets loaded from that jar. This causes
a `java.lang.NoSuchMethodError: io.netty.buffer.ByteBuf.forEachByte(Lio/netty/util/ByteProcessor;)I` which apparently
goes unhandled.
 
I noticed that the hang only happens when a remote control command is executed in the cleanup block.
 
To see it hang (gradle will stop at `0 tests completed`):

    ./gradlew test

and then Ctrl+C to kill it.
