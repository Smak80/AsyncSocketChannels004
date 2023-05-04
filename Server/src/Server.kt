import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Server(port: Int = 5004) {
    private val ssc: AsynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()
    private val socketAddress: SocketAddress = InetSocketAddress(port)
    private val mainServerCoroutine = CoroutineScope(Dispatchers.IO + Job())
    fun start(){
        mainServerCoroutine.launch {
            if (ssc.isOpen){
                ssc.bind(socketAddress)
                val sc = suspendCoroutine {
                    ssc.accept(it, AsyncHandler())
                }
                val buf = ByteBuffer.allocate(1024)
                suspendCoroutine {
                    sc.read(buf, it, AsyncHandler())
                }
                buf.flip()
                val ba = ByteArray(buf.limit())
                buf.get(ba)
                println(ba.toString(Charsets.UTF_8))
            }
        }
    }
}

class AsyncHandler<T>: CompletionHandler<T, Continuation<T>>{
    override fun completed(result: T, attachment: Continuation<T>) {
        attachment.resume(result)
    }
    override fun failed(exc: Throwable, attachment: Continuation<T>) {
        attachment.resumeWithException(exc)
    }
}

fun main() {
    Server().start()
    readln()
}