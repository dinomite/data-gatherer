# data-gatherer

A service runner to periodically gather data from disparate sources.

Data Gatherer is built around EventBus.

Data is produced by `UpdateProducer`s, which are periodically executed
by their encapsulating `EventBusScheduledService` which posts the
`List<Sensor>` that they produce to the event bus.  See
`ServiceRegistry` for where these are constructed.  

Data is sent to EmonCMS by `EmonReporter`, which is registered with
the event bus.
