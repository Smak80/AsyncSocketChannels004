import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Client(
    host: String,
    port: Int,
) {
    private val sc = AsynchronousSocketChannel.open()
    private val addr = InetSocketAddress(host, port)
    private val mainClientCoroutine = CoroutineScope(Dispatchers.IO + Job())

    fun start(){
        mainClientCoroutine.launch {
            if (sc.isOpen){
                suspendCoroutine {
                    sc.connect(addr, it, AsyncHandler())
                }
                val buf = ByteBuffer.allocate(1024)
                buf.put("Привет!".toByteArray(Charsets.UTF_8))
                buf.flip()
                suspendCoroutine {
                    sc.write(buf, it, AsyncHandler())
                }
                sc.shutdownInput()
                sc.shutdownOutput()
                sc.close()
            }
        }
    }
}

class AsyncHandler<T>: CompletionHandler<T, Continuation<T>> {
    override fun completed(result: T, attachment: Continuation<T>) {
        attachment.resume(result)
    }
    override fun failed(exc: Throwable, attachment: Continuation<T>) {
        attachment.resumeWithException(exc)
    }
}

fun main() {
    Client("localhost", 5004).start()
    readln()
}