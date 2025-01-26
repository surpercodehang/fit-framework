# Http High Level Messages

``` plantuml
@startuml
skinparam roundcorner 15
skinparam sequenceArrowThickness 2
skinparam defaultFontName Mono

hide empty members

interface HttpResourceSupplier {
+ {abstract} httpResource() : HttpResource
}
interface HttpResource {
+ {abstract} serializers() : Serializers
+ {abstract} valueFetcher() : ValueFetcher
}
interface HttpClient {
+ {abstract} createRequest(RequestMethod method) : HttpClassicClientRequest
+ {abstract} exchange(HttpClassicClientRequest request) : HttpClassicClientResponse
+ {abstract} exchange(HttpClassicClientRequest request, Class responseType) : HttpClassicClientResponse
}
interface HttpServer {
+ {abstract} bind(int port) : HttpServer
+ {abstract} start() : void
+ {abstract} stop() : void
+ {abstract} dispatcher() : HttpDispatcher
+ {abstract} send(HttpClassicServerResponse) : void
}

interface HttpMessage {
+ {abstract} httpVersion() : HttpVersion
+ {abstract} headers() : MessageHeaders
+ {abstract} transferEncoding() : Optional<String>
+ {abstract} contentType() : Optional<ContentType>
+ {abstract} contentLength() : int
+ {abstract} cookies() : CookieCollection
+ {abstract} entity() : Optional<Entity>
+ {abstract} entitySerializer() : EntitySerializer<Entity>
+ {abstract} isCommitted() : boolean
}
interface HttpClassicRequest {
+ {abstract} method() : RequestMethod
+ {abstract} requestUri() : String
+ {abstract} host() : String
+ {abstract} path() : String
+ {abstract} queries() : QueryCollection
}
interface HttpClassicClientRequest {
+ {abstract} headers() : ConfigurableMessageHeaders
+ {abstract} cookies() : ConfigurableCookieCollection
+ {abstract} entity(Entity entity) : void
+ {abstract} formEntity(MultiValueMap<String, String> form) : void
+ {abstract} jsonEntity(Object jsonObject) : void
+ {abstract} exchange() : HttpClassicClientResponse
+ {abstract} exchange(Class responseType) : HttpClassicClientResponse
}
interface HttpClassicServerRequest {
+ {abstract} attributes() : AttributeCollection
+ {abstract} localAddress() : Address
+ {abstract} remoteAddress() : Address
+ {abstract} isSecure() : boolean
}
interface HttpClassicResponse {
+ {abstract} statusCode() : int
+ {abstract} reasonPhrase() : String
}
interface HttpClassicClientResponse {
+ {abstract} objectEntity() : Optional<ObjectEntity<Object>>
+ {abstract} textEntity() : Optional<TextEntity>
}
interface HttpClassicServerResponse {
+ {abstract} statusCode(int statusCode) : void
+ {abstract} reasonPhrase(String reasonPhrase) : void
+ {abstract} headers() : ConfigurableMessageHeaders
+ {abstract} cookies() : ConfigurableCookieCollection
+ {abstract} entity(Entity entity) : void
+ {abstract} writableBinaryEntity() : WritableBinaryEntity
+ {abstract} send() : void
}

interface Serializers {
+ {abstract} json() : Optional<ObjectSerializer>
+ {abstract} entities() : Map<MimeType, EntitySerializer<Entity>>
}

interface Entity {
+ {abstract} belongTo() : HttpMessage
}

HttpResource <|-- HttpClient
HttpResource <|-- HttpServer
HttpResource ..> Serializers

HttpResourceSupplier ..> HttpResource
HttpResourceSupplier <|-- HttpMessage
HttpMessage <|-- HttpClassicRequest
HttpClassicRequest <|-- HttpClassicClientRequest
HttpClassicRequest <|-- HttpClassicServerRequest
HttpMessage <|-- HttpClassicResponse
HttpClassicResponse <|-- HttpClassicClientResponse
HttpClassicResponse <|-- HttpClassicServerResponse

HttpMessage ..> Entity
Entity ..> HttpMessage

@enduml
```

# Http Entities

``` plantuml
@startuml
skinparam roundcorner 15
skinparam sequenceArrowThickness 2
skinparam defaultFontName Mono

hide empty members

interface Entity {
+ {abstract} belongTo() : HttpMessage
}
interface ReadableBinaryEntity {
+ read(byte[] bytes) : int
+ {abstract} read(byte[] bytes, int off, int len) : int
}
interface FileEntity {
+ {abstract} filename() : String
}
interface WritableBinaryEntity {
+ write(byte[] bytes) : void
+ {abstract} write(byte[] bytes, int off, int len) : void
+ {abstract} flush() : void
}
interface MultiValueEntity {
+ {abstract} keys() : List<String>
+ {abstract} first(String key) : Optional<String>
+ {abstract} all(String key) : List<String>
+ {abstract} size() : int
}
interface NamedEntity {
+ {abstract} name() : String
+ {abstract} entity() : Entity
}
interface ObjectEntity {
+ {abstract} object() : Object
}
interface PartitionedEntities {
+ {abstract} entities() : List<Entity>
}
interface TextEntity {
+ {abstract} content() : String
}
abstract AbstractEntity {
- HttpMessage httpMessage
+ belongTo() : HttpMessage
}
class DefaultReadableBinaryEntity {
- byte[] bytes
- int pos
+ read(byte[] bytes) : int
+ read(byte[] bytes, int off, int len) : int
}
class DefaultFileEntity {
- String filename
+ filename() : String
}
class DefaultWritableBinaryEntity {
- ServerResponse serverResponse
+ write(byte[] bytes, int off, int len) : void
+ flush() : void
}
class DefaultMultiValueEntity {
- MultiValueMap<String, String> values
+ keys() : List<String>
+ first(String key) : Optional<String>
+ all(String key) : List<String>
+ size() : int
}
class DefaultNamedEntity {
- String name
- Entity entity
+ name() : String
+ entity() : Entity
}
class DefaultObjectEntity {
- Object obj
+ object() : Object
}
class DefaultPartitionedEntities {
- List<Entity> entities
+ entities() : List<Entity>
}
class DefaultTextEntity {
- String content
+ content() : String
}

Entity <|-- ReadableBinaryEntity
ReadableBinaryEntity <|-- FileEntity
Entity <|-- WritableBinaryEntity
Entity <|-- MultiValueEntity
Entity <|-- NamedEntity
Entity <|-- ObjectEntity
Entity <|-- PartitionedEntities
Entity <|-- TextEntity
Entity <|... AbstractEntity
ReadableBinaryEntity <|... DefaultReadableBinaryEntity
AbstractEntity <|-- DefaultReadableBinaryEntity
FileEntity <|... DefaultFileEntity
DefaultReadableBinaryEntity <|-- DefaultFileEntity
WritableBinaryEntity <|... DefaultWritableBinaryEntity
AbstractEntity <|-- DefaultWritableBinaryEntity
MultiValueEntity <|... DefaultMultiValueEntity
AbstractEntity <|-- DefaultMultiValueEntity
NamedEntity <|... DefaultNamedEntity
AbstractEntity <|-- DefaultNamedEntity
ObjectEntity <|... DefaultObjectEntity
AbstractEntity <|-- DefaultObjectEntity
PartitionedEntities <|... DefaultPartitionedEntities
AbstractEntity <|-- DefaultPartitionedEntities
TextEntity <|... DefaultTextEntity
AbstractEntity <|-- DefaultTextEntity

@enduml
```