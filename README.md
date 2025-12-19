# JavaZPL2PDF

A powerful Java library to convert ZPL (Zebra Programming Language) to PDF.

## Features
- Convert complex ZPL labels to high-quality PDF.
- Support for barcodes (1D and 2D like QR, MaxiCode).
- Customizable dimensions (inches, mm, cm) and DPI.
- Generates standard PDF using Apache PDFBox.

## Installation

### Maven
Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.varunsermaldev</groupId>
    <artifactId>JavaZPL2PDF</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'io.github.varunsermaldev:JavaZPL2PDF:1.0.0'
```

## Usage

The library provides a simple entry point through the `ZPLRenderer` class.

### Simple Example

```java
import com.zpl2pdf.ZPLRenderer;
import com.zpl2pdf.ZPLConfig;

public class MyLabelPrinter {
    public static void main(String[] args) throws Exception {
        // 1. Define ZPL Data
        String zplData = "^XA^FO50,50^A0N,50,50^FDHello World^FS^XZ";

        // 2. Prepare Config (4x6 inch, 203 DPI)
        ZPLConfig config = ZPLConfig.default4x6();

        // 3. Render directly to a file
        ZPLRenderer.renderWithConfig(zplData, config, "output.pdf");
        
        // OR render to byte array
        byte[] pdfBytes = ZPLRenderer.renderToPdfBytes(zplData, config);

        // OR render to an OutputStream (e.g., FileOutputStream)
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream("stream_output.pdf")) {
            ZPLRenderer.renderToStream(zplData, config, fos);
        }
    }
}
```

### Custom Configuration

```java
ZPLConfig config = new ZPLConfig(10, 15, "cm", 300);
ZPLRenderer.renderWithConfig(zplData, config, "custom_label.pdf");
```

## How to Publish to Maven Central

To publish this library to Maven Central, follow these steps:

1. **Configure OSSRH Credentials**: Add your Sonatype credentials to `~/.m2/settings.xml`.
2. **GPG Key**: Ensure you have a GPG key generated and published to a keyserver.
3. **Execute Publish Command**:

```powershell
mvn clean verify -Prelease org.sonatype.central:central-publishing-maven-plugin:publish
```

> [!IMPORTANT]
> **Release Requirements:**
> 1. **GPG Installed:** You must have GPG installed and in your PATH. If you get a "gpgVersion is null" error, ensure `gpg --version` works in your terminal.
> 2. **OSSRH Credentials:** Ensure your `~/.m2/settings.xml` has the `<server>` entry for `central`.
