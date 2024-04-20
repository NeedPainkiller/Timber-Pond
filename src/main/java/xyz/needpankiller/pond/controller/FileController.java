package xyz.needpankiller.pond.controller;

 import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestHeader;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.needpankiller.pond.dto.FileInfo;
import xyz.needpankiller.pond.error.FileException;
import xyz.needpankiller.pond.lib.security.JsonWebTokenProvider;
import xyz.needpankiller.pond.lib.security.TokenValidFailedException;
import xyz.needpankiller.pond.lib.storage.NormalStorageService;
import xyz.needpankiller.pond.service.FileService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static xyz.needpankiller.pond.lib.exceptions.CommonErrorCode.TOKEN_HEADER_MUST_REQUIRED;
import static xyz.needpankiller.pond.lib.security.JsonWebTokenProvider.BEARER_TOKEN_HEADER;

@Path("/api/files")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);

    @Inject
    FileService fileService;

    @Inject
    NormalStorageService storageService;

    @Inject
    JsonWebTokenProvider jsonWebTokenProvider;


    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(@RestHeader(BEARER_TOKEN_HEADER) String token, MultipartFormDataInput input) {
        if (token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(TOKEN_HEADER_MUST_REQUIRED).build();
        }
        jsonWebTokenProvider.validateToken(token);
        Long userPk = jsonWebTokenProvider.getUserPk(token);

        List<FileInfo> fileInfoList = fileService.parseFileInfo(input);
        logger.info("file upload : {}", fileInfoList.size());
        for (FileInfo fileInfo : fileInfoList) {
            try {
                storageService.store(fileInfo);
                fileInfo.setCreatedBy(userPk);
                fileInfo.setFileExists(true);
                fileService.sendFileStoredMessage(fileInfo);
            } catch (FileException e) {
                storageService.remove(fileInfo);
                logger.error("file upload failed : {} | {}", e.getClass(), e.getMessage());
            }
        }
        return Response.ok(fileInfoList.stream().filter(FileInfo::isFileExists).toList()).build();
    }

    @GET
    @Produces("application/octet-stream")
    @Path("/{UUID}")
    public Response download(@RestHeader(BEARER_TOKEN_HEADER) String token, @PathParam("UUID") String uuid) throws IOException {
        if (token.isEmpty()) {
            throw new TokenValidFailedException(TOKEN_HEADER_MUST_REQUIRED);
        }
        jsonWebTokenProvider.validateToken(token);
        Long userPk = jsonWebTokenProvider.getUserPk(token);

        logger.info("download file : {}", uuid);
        FileInfo fileInfo = fileService.selectFile(uuid, userPk);

        String originalFileName = fileInfo.getOriginalFileName().trim();
        String fileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        String contentDisposition = "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ";";


        return Response.ok(storageService.load(fileInfo))
                .header("Content-Disposition", contentDisposition)
                .build();

    }

/*    @GET
    @Produces("application/octet-stream")
    @Path("/async/{UUID}")
    public Uni<Response> downloadAsync(@RestHeader(BEARER_TOKEN_HEADER) String token, @PathParam("UUID") String uuid) throws IOException {
        if (token.isEmpty()) {
            throw new TokenValidFailedException(TOKEN_HEADER_MUST_REQUIRED);
        }
        jsonWebTokenProvider.validateToken(token);
        Long userPk = jsonWebTokenProvider.getUserPk(token);

        logger.info("download file : {}", uuid);
        Uni<FileInfo> fileInfoUni = fileService.selectFileReactive(uuid, userPk);

        Uni<String> fileNameUni = fileInfoUni
                .map(FileInfo::getOriginalFileName).map(String::trim)
                .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8).replaceAll("\\+", "%20"))
                .map(s -> "attachment; filename=" + new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ";");

        Uni<StreamingOutput> streamingOutputUni = fileInfoUni.map(storageService::load);

        return Uni.combine().all().unis(fileNameUni, streamingOutputUni)
                .asTuple().map(tuple -> {
                    String contentDisposition = tuple.getItem1();
                    StreamingOutput streamingOutput = tuple.getItem2();
                    return Response.ok(streamingOutput)
                            .header("Content-Disposition", contentDisposition)
                            .build();
                });
    }*/

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok().build();
    }
}
