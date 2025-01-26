# ZIP format

[APPNOTE.TXT - .ZIP File Format Specification](https://pkware.cachefly.net/webdocs/casestudies/APPNOTE.TXT)

[local file header 1]  
[encryption header 1]  
[file data 1]  
[data descriptor 1]  
.  
.  
.  
[local file header n]  
[encryption header n]  
[file data n]  
[data descriptor n]  
[archive decryption header]   
[archive extra data record]   
[central directory header 1]  
.  
.  
.  
[central directory header n]  
[zip64 end of central directory record]  
[zip64 end of central directory locator]   
[end of central directory record]

## Local File Header (0x04034b50)

| Description | Offset | Length |
|---|---|---|
| local file header signature | 0 | 4 |
| version needed to extract | 4 | 2 |
| general purpose bit flag | 6 | 2 |
| compression method | 8 | 2 |
| last mod file time | 10 | 2 |
| last mod file date | 12 | 2 |
| crc-32 | 14 | 4 |
| compressed size | 18 | 4 |
| uncompressed size | 22 | 4 |
| file name length | 26 | 2 |
| extra field length | 28 | 2 |
| file name | 30 | *variable size* |
| extra field | 30 + *file name* | *variable size* |

If bit 3 of the `general purpose bit flag` is set, `crc-32`, `compressed size` and `uncompressed size` are set to zero
in the local header and the correct values are put in the data descriptor and in the central directory.

## Central Directory

[File Header 1]  
.  
.  
.  
[File Header n]  
[Digital Signature]

### File Header (0x02014b50)

| Description | Offset | Length |
|---|---|---|
| central file header signature | 0 | 4 |
| version made by | 4 | 2 |
| version needed to extract | 6 | 2 |
| general purpose bit flag | 8 | 2 |
| compression method | 10 | 2 |
| last mod file time | 12 | 2 |
| last mod file date | 14 | 2 |
| crc-32 | 16 | 4 |
| compressed size | 20 | 4 |
| uncompressed size | 24 | 4 |
| file name length | 28 | 2 |
| extra field length | 30 | 2 |
| file comment length | 32 | 2 |
| disk number start | 34 | 2 |
| internal file attributes | 36 | 2 |
| external file attributes | 38 | 4 |
| relative offset of local header | 42 | 4 |
| file name | 46 | *variable size* |
| extra field | 46 + *file name length* | *variable size* |
| file comment | 46 + *file name length* + *extra field length* | *variable size* |

#### Zip64 Extended Information Extra Field (0x0001)

| Description | Offset | Length |
|---|---|---|
| Tag for this "extra" block type | 0 | 2 |
| Size of this "extra" block | 2 | 2 |
| Original uncompressed file size | 4 | 8 |
| Size of compressed data | 12 | 8 |
| Offset of local header record | 20 | 8 |
| Number of the disk on which this file starts | 28 | 4 |

### Digital Signature (0x05054b50)

| Description | Offset | Length |
|---|---|---|
| header signature | 0 | 4 |
| size of data | 4 | 2 |
| signature data | 6 | *variable size*|

## Zip64 end of central directory record (0x06064b50)

| Description | Offset | Length |
|---|---|---|
| zip64 end of central dir signature | 0 | 4 |
| size of zip64 end of central directory record | 4 | 8 |
| version made by | 12 | 2 |
| version needed to extract | 14 | 2 |
| number of this disk | 16 | 4 |
| number of the disk with the start of the central directory | 20 | 4 |
| total number of entries in the central directory on this disk | 24 | 8 |
| total number of entries in the central directory | 32 | 8 |
| size of the central directory | 40 | 8 |
| offset of start of central directory with respect to the starting disk number | 48 | 8 |
| zip64 extensible data sector | 56 | *variable size* |

## Zip64 end of central directory locator (0x07064b50)

| Description | Offset | Length |
|---|---|---|
| zip64 end of central dir locator signature | 0 | 4 |
| number of the disk with the start of the zip64 end of  central directory | 4 | 4 |
| relative offset of the zip64 end of central directory record | 8 | 8 |
| total number of disks | 16 | 4 |

## End of central directory record (0x06054b50)

| Description | Offset | Length |
|---|---|---|
| end of central dir signature | 0 | 4 |
| number of this disk | 4 | 2 |
| number of the disk with the start of the central directory | 6 | 2 |
| total number of entries in the central directory on this disk | 8 | 2 |
| total number of entries in the central directory | 10 | 2 |
| size of the central directory | 12 | 4 |
| offset of start of central directory with respect to the starting disk number | 16 | 4 |
| .ZIP file comment length | 20 | 2 |
| .ZIP file comment | 22 | *variable size* |

## 字段解释

### 时间与日期

The date and time are encoded in standard MS-DOS format. If input came from standard input, the date and time are those
at which compression was started for this data. If encrypting the central directory and general purpose bit flag 13 is
set indicating masking, the value stored in the Local Header will be zero. MS-DOS time format is different from more
commonly used computer time formats such as UTC. For example, MS-DOS uses year values relative to 1980 and 2 second
precision.

> 日期与时间使用标准的MS-DOS格式编码，其数据布局为：  
> ![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2g3c)
> 其中`year`表示1980年后的年数，即当其值为`0`时，表示1980年。


[RFC 1951: DEFLATE Compressed Data Format Specification version 1.3](https://www.ietf.org/rfc/rfc1951.txt)

## 建模

``` plantuml
component DataBlock
component Jar
component JarEntry

DataBlock - Jar
Jar - JarEntry
```

- `DataBlock`，表示数据块，定义数据块与数据子块的结构，同时具备数据范围的界定，防止超出访问的范围。
- `Jar`，表示Java归档件，提供对外的基础能力。
- `JarEntry`，表示Java归档件中的条目，提供基础的对外能力。

> `DataBlock`与`Jar`都存在嵌套关系，但是`DataBlock`仅表示数据，在形成嵌套的过程中会被打平，以提升数据访问性能。即多层次嵌套会被打平成为单层次嵌套。
>
> `Jar`的嵌套更多表现归档件的实际的层次关系，因此其嵌套关系根据实际情况可能存在多层次嵌套的情况。
>
> `Jar`可能使用`DataBlock`作为数据源，此时二者间的嵌套关系可能存在差异。

``` plantuml
hide empty members

skinparam backgroundColor #EEEBDC
skinparam roundcorner 20
skinparam sequenceArrowThickness 2
skinparam ClassFontName Consolas

interface Jar {
+ {abstract} file() : File
+ {abstract} name() : String
+ {abstract} parent() : Jar
+ {abstract} url() : URL
+ {abstract} comment() : String
+ {abstract} countEntries() : int
+ {abstract} getEntry(String name) : Jar$Entry
+ {abstract} getEntry(int index) : Jar$Entry
+ {abstract} closed() : boolean
+ {static} fromFile(File) : CloseableJar
}

interface Jar$Entry {
+ {abstract} jar() : Jar
+ {abstract} url() : URL
+ {abstract} name() : String
+ {abstract} compressionMethod() : CompressionMethod
+ {abstract} crc32() : int
+ {abstract} offset() : long
+ {abstract} compressedSize() : long
+ {abstract} uncompressedSize() : long
+ {abstract} lastModificationTime() : Date
+ {abstract} comment() : String
+ {abstract} directory() : boolean
+ {abstract} getInputStream() : InputStream
+ {abstract} content() : DataBlock
+ {abstract} asJar() : Jar
}
Jar *-right-> Jar$Entry

abstract AbstractDataBlockJar {
- data : DataBlock
- comment : String
- entries : JarEntryCollection
- closed : boolean
+ AbstractDataBlockJar(DataBlock data)
# data() : DataBlock
+ countEntries() : int
+ getEntry(String name) : Jar$Entry
+ getEntry(int index) : Jar$Entry
+ comment() : String
+ iterator() : Iterator<Jar$Entry>
+ close() : void
# {abstract} close0() : void
+ closed() : boolean
+ equals(Object) : boolean
+ hashCode() : int
+ toString() : String
}
AbstractDataBlockJar ..up.|> Jar
note left of Jar::closed
The <color:blue><b>java.util.Closeable</b></color> can be implemented only by
<color:blue><b>Jar</b></color>, but not by <color:blue><b>DataBlock</b></color>. Because not all JARs are
based on a <color:blue><b>DataBlock</b></color>, there are directory-based ones.
Such as the <color:blue><b>NestedDirectoryJar</b></color>, it is opened without
any new references of <color:blue><b>DataBlock</b></color>, and it cannot be
observed by any another <color:blue><b>DataBlock</b></color>s.
end note
java.util.Closeable ()- Jar

abstract AbstractJarEntry {
+ url() : URL
+ directory() : boolean
+ asJar() : Jar
+ equals(Object) : boolean
+ hashCode() : int
+ toString() : String
}
AbstractJarEntry ..up.|> Jar$Entry

class AbstractDataBlockJar$Entry {
- jar : AbstractDataBlockJar
- name : String
- compressionMethod : CompressionMethod
- crc32 : int
- compressedSize : int
- uncompressedSize : int
- lastModificationTime : Date
- comment : String
- offset : long
- content : DataBlock
+ AbstractDataBlockJar$Entry(AbstractDataBlockJar, CentralDirectoryFileHeader)
+ jar() : Jar
+ name() : String
+ compressionMethod() : CompressionMethod
+ crc32() : int
+ compressedSize() : long
+ uncompressedSize() : long
+ lastModificationTime() : Date
+ comment() : String
+ getInputStream() : InputStream
+ offset() : long
+ content() : DataBlock
+ asJar() : Jar
}
AbstractDataBlockJar$Entry -|> AbstractJarEntry
AbstractDataBlockJar "1" *-> "n" AbstractDataBlockJar$Entry

class RootJar {
+ RootJar(File file)
+ file() : File
+ name() : String
+ parent() : Jar
+ url() : URL
+ close() : void
}
RootJar -up-|> AbstractDataBlockJar

class NestedJar {
- entry : Jar$Entry
+ NestedJar(Jar$Entry)
+ file() : File
+ name() : String
+ parent() : Jar
+ url() : URL
}
NestedJar -up-|> AbstractDataBlockJar

class NestedDirectoryJar {
- entry : Jar$Entry
- entries : JarEntryCollection
+ NestedDirectoryJar(Jar$Entry)
+ file() : File
+ name() : String
+ parent() : Jar
+ url() : URL
+ comment() : String
+ countEntries() : int
+ getEntry(String name) : Jar$Entry
+ getEntry(int index) : Jar$Entry
}
NestedDirectoryJar .up.|> Jar

class NestedDirectoryJar$Entry {
- origin : Jar$Entry
- name : String
+ NestedDirectoryJar$Entry(Jar$Entry)
+ name() : String
+ compressionMethod() : CompressionMethod
+ crc32() : int
+ offset() : long
+ compressedSize() : long
+ uncompressedSize() : long
+ lastModificationTime() : Date
+ comment() : String
+ getInputStream() : InputStream
+ content() : DataBlock
}
NestedDirectoryJar$Entry --|> AbstractJarEntry
NestedDirectoryJar *-right-> NestedDirectoryJar$Entry

interface DataBlock {
+ {abstract} size() : long
+ {abstract} read(long) : int
+ {abstract} read(long, int) : byte[]
+ {abstract} read(long, byte[], int, int) : int
+ {abstract} u2(long) : long
+ {abstract} u4(long) : long
+ {abstract} u8(long) : long
+ {abstract} text() : String
+ {abstract} asInputStream() : InputStream
+ {abstract} sub(long, long) : DataBlock
+ {static} close(DataBlock) : void
}
AbstractDataBlockJar "1" o-down-> "1" DataBlock
AbstractDataBlockJar$Entry "1" *-down-> "1" DataBlock

abstract AbstractDataBlock {
+ read(long) : int
+ read(long, int) : byte[]
+ read(long, byte[], int, int) : int
+ u2(long) : long
+ u4(long) : long
+ u8(long) : long
+ text() : String
+ asInputStream() : InputStream
+ sub(long, long) : DataBlock
# {abstract} read0(long) : int
# {abstract} read0(long, int) : byte[]
# {abstract} read0(long, byte[], int, int) : int
}
AbstractDataBlock .left.|> DataBlock

class FileDataBlock {
- file : File
- access : RandomAccessFile
- lock : Object
+ FileDataBlock(File)
file() : File
+ size() : long
# read0(long) : int
# read0(long, int) : byte[]
# read0(long, byte[], int, int) : int
+ close() : void
+ equals(Object) : boolean
+ hashCode() : int
+ toString() : String
}
FileDataBlock -up-|> AbstractDataBlock

class NestedDataBlock {
- parent : AbstractDataBlock
- offset : long
- size : long
NestedDataBlock(AbstractDataBlock, long, long)
parent() : AbstractDataBlock
offset() : long
+ size() : long
# read0(long) : int
# read0(long, int) : byte[]
# read0(long, byte[], int, int) : int
+ equals(Object) : boolean
+ hashCode() : int
+ toString() : String
}
NestedDataBlock -up-|> AbstractDataBlock

class NestableJarFile<<adapter>> {
- jar : Jar
- entries : Map<String, NestableJarFile$Entry>

+ getJarEntry(String) : Entry
+ getEntry(String) : Entry
+ entries() : Enumeration<JarEntry>
+ stream() : Stream<JarEntry>
+ getInputStream(ZipEntry) : InputStream
+ getComment() : String
+ getName() : String
+ size() : int
+ close() : void
}
NestableJarFile o-down-> Jar

class NestableJarFile$Entry<<adapter>> {
- entry : Jar$Entry
- NestableJarFile$Entry(Jar$Entry)
+ getName() : String
+ getTime() : long
+ getSize() : long
+ getCompressedSize() : long
+ getCrc() : long
+ getMethod() : int
+ getComment() : String
+ isDirectory() : boolean
}
NestableJarFile$Entry o-down-> Jar$Entry
NestableJarFile "1" *-> "n" NestableJarFile$Entry
```

# 缓存设计

可嵌套的 JAR 是一个树形结构，根节点是通过文件本身加载的 JAR，其中包含的若干个条目可各自形成嵌套 JAR，并依此递归。

因此，缓存的设计也使用树形结构，从而更好地反应现实结构。其中树中的任意节点（称为缓存节点）均为一个 JAR。

对于任意缓存节点，应具备以下能力：

- 调用方可通过节点获取一个针对 JAR 的引用
- 当一个缓存节点关联多个引用时，其应使用同一个 JAR 实例
- 当一个缓存节点未关联任何引用，且其不存在任何子节点时，应释放该节点，回收资源

``` plantuml
hide empty members

skinparam backgroundColor #EEEBDC
skinparam roundcorner 20
skinparam sequenceArrowThickness 2
skinparam ClassFontName Consolas

skinparam class {
BackgroundColor<<REFERENCE>> LightGray
BackgroundColor<<EXTENSION CAPABILITIES>> Pink
}

interface Jar<<REFERENCE>> {

}

class JarCache {
+ roots : ConcurrentMap<<color:blue><u>JarLocation</u></color>, <color:blue><u>Jar</u></color>>
+ acquire(JarLocation) : <color:blue><u>Jar</u></color>
}

class JarCache$Node {
- jar : <color:blue><u>Jar</u></color>
- children : List<<color:blue><u>JarCache$Node</u></color>>
- references : List<<color:blue><u>JarReference</u></color>>
+ acquire() : <color:blue><u>JarReference</u></color>

}

class JarCache$NodeCollection {
- nodes : Map<Object, <color:blue><u>JarCache$Node</u></color>>

+ 
}

class JarReference {
}
JarReference -() Closeable

JarCache$Node "1" *-right-> "n" JarReference : manages
JarCache "1" *-down-> "n" JarCache$Node : manages
JarCache .right.> Jar : returns
JarReference .up.|> Jar : decorates
```