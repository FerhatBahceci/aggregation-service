# Getting Started

### Reference Documentation
For further reference, please consider the following sections:



Collect incoming values into multiple List buffers that will be emitted by the returned Flux every bufferingTimespan.

Discard Support: This operator discards the currently open buffer upon cancellation or error triggered by a data signal.
Params:
bufferingTimespan – the duration from buffer creation until a buffer is closed and emitted
Returns:
a microbatched Flux of List delimited by the given time span




Split this Flux sequence into multiple Flux windows containing maxSize elements (or less for the final window) and starting from the first item. Each Flux window will onComplete once it contains maxSize elements OR it has been open for the given Duration (as measured on the parallel Scheduler).



http://localhost:8081/aggregation?pricing=AF,AL,DZ,AS,AD,AO,AI,AQ,AG,AR,AM,AW,AU,AT,AZ,BS,BH,BD,BB,BY,BZ,SE,BE,ES,NO,DK,IT,PL&track=123456799,123456791,123456792,123456793,123456794,123456795,123456796,123456797,123456798,123456719,123456729,123456739,123456749,123456759,123456769,123456789,223456799,323456799,423456799,523456799,623456799,723456799,823456799,125456799,123656799,123466799&shipments=123456799,123456791,123456792,123456793,123456794,123456795,123456796,123456797,123456798,123456719,123456729,123456739,123456749,123456759,123456769,123456789,223456799,323456799,423456799,523456799,623456799,723456799,823456799,125456799,123656799,123466799