Parallel IP Address Processing

Overview

The task was to come up with a way to process large files containing IP addresses and find the number of unique IPs. 
The challenge was to make this faster than simply reading the file line by line and adding each IP to a HashSet, which operates in linear time O(N). 
Improving the asymptomatic complexity beyond linear (aka to binary time) isn’t really possible here, so my idea is to optimize the execution time by using parallelism, especially when dealing with larger files.

Thought Process

Why Parallelism?

The first thing I had to figure out was whether I could improve on the linear approach of adding IPs to a HashSet. Since adding elements to a HashSet is already O(N), 
I knew that I couldn’t reduce the complexity itself. Instead, I focused on how to speed up the actual runtime by splitting the workload across multiple threads.
For small files, adding IPs to a HashSet sequentially is actually faster because there’s no overhead from thread management. But for large files (let's say over 100 MB),
the cost of reading and processing lines starts to add up, and that’s when parallelism really pays off.

Why I didn't split file into several chunks for parallel processing

My first thought was to divide the file into several parts, assuming that the average IP address is 16 bytes. 
I could split the file into chunks divisible by 16 bytes and feed those chunks to virtual threads.
However, IP addresses are different sizes, and I could implement this approach only by sacrificing accuracy, as some IPs might get lost, which I assume is not an option here.

Choosing ConcurrentHashMap

To handle the case where multiple threads might be adding IP addresses at the same time, I decided to use my favourite structure -- ConcurrentHashMap. 
This is because ConcurrentHashMap allows concurrent reads and writes without the need for explicit synchronization which allows locking individual buckets instead of the whole map.
I treated the ConcurrentHashMap like a Set by storing the IP addresses as keys and using a constant placeholder (PRESENT) as the value. 
Instead of creating a new object every time I insert an IP (like new Object()). This prevents unnecessary memory usage, which could be significant if we’re processing millions of IPs. 
By using a constant, I’m saving on the overhead of creating and storing millions of identical objects.
Additionaly, putIfAbsent() method allows to save resources By avoiding the rewriting of existing keys.

Why I Didn’t Use Virtual Threads

I chose not to use virtual threads (coroutines) for this task because parallel streams offer internal thread management, which greatly simplifies the code. 
Since the task involves straightforward operations—reading lines and adding them to a map—there is no need for complex thread synchronization or blocking.
Virtual threads would only be beneficial if the task required managing such coordination, but for this case, parallel streams are more efficient and easier to implement.

Fork/Join

Parallel streams (Fork/Join) are well-suited for processing a large number of small, similar tasks concurrently.

Time Complexity

The time complexity of this solution is still O(N) because we’re processing every line once. But by processing those lines in parallel, we reduce the total time it takes for large files. 
The speedup comes from being able to handle multiple lines of the file at once.

Other Considerations

Thread Safety: The ConcurrentHashMap ensures that multiple threads can safely insert IPs without collisions or race conditions. 
Each IP will only be added once, even if multiple threads encounter the same IP at the same time.

Final Thoughts

In conclusion, this approach works best for large files because the overhead of thread management only makes sense when the file size justifies it. 
For smaller files, a simple HashSet will be faster. But for files 100 MB or larger, this parallel approach speeds up processing by leveraging multiple threads and concurrent data structures. 
The ConcurrentHashMap ensures thread safety and efficiency, while the use of a constant object minimizes memory overhead. The overall complexity remains O(N), but with much better performance for large datasets.

