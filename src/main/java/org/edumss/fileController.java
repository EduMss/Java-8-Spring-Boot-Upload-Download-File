package org.edumss;


import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class fileController {

    @PostMapping
    public ResponseEntity<String> uploadArquivo(@RequestParam("file") @NotNull MultipartFile file) {
        // Verifique se o arquivo não está vazio
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo não pode estar vazio.");
        }

        try {
            // Obtenha o nome original do arquivo
            String fileName = file.getOriginalFilename();

            // Obtenha o caminho para salvar o arquivo (substitua o caminho com seu diretório desejado)
            Path destinationDirectory = Paths.get("C:\\Download\\");

            // Verifique se o diretório existe, se não, crie-o
            if (!Files.exists(destinationDirectory)) {
                Files.createDirectories(destinationDirectory);
            }

            // Caminho completo para o arquivo de destino
            Path destinationPath = destinationDirectory.resolve(fileName);

            // Copie o conteúdo do arquivo para o destino
            file.transferTo(destinationPath.toFile());

            return ResponseEntity.ok("Arquivo enviado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao processar o arquivo.");
        }
    }


    @GetMapping("/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Path filePath = Paths.get("C:\\Download\\" + filename);
        boolean fileExists = Files.exists(filePath);
        if(fileExists) {
            // Lógica para obter o conteúdo do arquivo
            byte[] fileContent = getFileContent(filename);

            // Crie um recurso ByteArrayResource para o conteúdo do arquivo
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            // Configure os cabeçalhos da resposta
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileContent.length));

            // Crie uma resposta ResponseEntity com o recurso e os cabeçalhos
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // Método de exemplo para obter o conteúdo do arquivo (substitua com sua lógica)
    private byte[] getFileContent(String filename) {
        // Lógica para obter o conteúdo do arquivo a partir do seu sistema de arquivos, banco de dados, etc.
        Path destinationDirectory = Paths.get("C:\\Download\\");
        // Caminho completo para o arquivo de destino
        Path destinationPath = destinationDirectory.resolve(filename);
        try {
            byte[] fileBytes = Files.readAllBytes(destinationPath);
            return fileBytes;

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[]{/* bytes do arquivo */};
        }
    }

    @GetMapping("/visualizar/{filename}")
    public ResponseEntity<Resource> visualizarArquivo(@PathVariable String filename) {
        Path filePath = Paths.get("C:\\Download\\" + filename);
        boolean fileExists = Files.exists(filePath);
        if(fileExists) {
            try {
                //Obtendo os bytes do arquivo
                byte[] fileContent = getFileContent(filename);

                // Use o método probeContentType para obter o tipo de arquivo
                String fileType = Files.probeContentType(filePath);

                if (fileType != null) {
                    // Crie um recurso ByteArrayResource para o conteúdo do arquivo
                    ByteArrayResource resource = new ByteArrayResource(fileContent);
                    // Crie uma resposta ResponseEntity com o recurso e os cabeçalhos
                    return ResponseEntity.ok()
                            .header("Content-Type", fileType)
                            .header("Accept-Ranges", "bytes")
                            .header("Content-Length", String.valueOf(resource.contentLength() - 1))
                            .body(resource);
                }
            } catch (IOException e) {
                // Lidar com exceções de leitura do arquivo, se necessário
                e.printStackTrace();
            }
        }
        // Crie uma resposta ResponseEntity com o recurso e os cabeçalhos
        return ResponseEntity.badRequest().build();
    }
}
