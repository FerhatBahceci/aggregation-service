# Getting Started

### Reference Documentation
For further reference, please consider the following sections:


For instance, by default, it is unbounded:
if you push any amount of data through it while its 
Subscriber has not yet requested data, it buffers all 
of the data. You can change this by providing a custom 
Queue implementation for the internal buffering
in the Sinks.many().unicast().onBackpressureBuffer(Queue)
factory method. If that queue is bounded, the sink could 
reject the push of a value when the buffer is full and
not enough requests from downstream have been received.

http://localhost:8081/aggregation?pricing=AF,AL,DZ,AS,AD,AO,AI,AQ,AG,AR,AM,AW,AU,AT,AZ,BS,BH,BD,BB,BY,BZ,SE,BE,ES,NO,DK,IT,PL&track=123456799,123456791,123456792,123456793,123456794,123456795,123456796,123456797,123456798,123456719,123456729,123456739,123456749,123456759,123456769,123456789,223456799,323456799,423456799,523456799,623456799,723456799,823456799,125456799,123656799,123466799&shipments=123456799,123456791,123456792,123456793,123456794,123456795,123456796,123456797,123456798,123456719,123456729,123456739,123456749,123456759,123456769,123456789,223456799,323456799,423456799,523456799,623456799,723456799,823456799,125456799,123656799,123466799