import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler

class Server(val port: Int = 5004) {
    private val ssc: AsynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()
    private val socketAddress: SocketAddress = InetSocketAddress(port)
    private val mainServerCoroutine = CoroutineScope(Dispatchers.IO + Job())
    fun startAsync(){
        mainServerCoroutine.launch {
            if (ssc.isOpen){
                ssc.bind(socketAddress)
                launch {
                    ssc.accept(null, object : CompletionHandler<AsynchronousSocketChannel, Any?>{
                        override fun completed(result: AsynchronousSocketChannel?, attachment: Any?) {
                            TODO("Not yet implemented")
                        }

                        override fun failed(exc: Throwable?, attachment: Any?) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }
        }
    }
}