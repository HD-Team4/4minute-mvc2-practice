package kr.or.bit.ajax;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import kr.or.bit.dto.BoardDTO;
import kr.or.bit.service.BoardService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@WebServlet("/board/api/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 100 * 1024 * 1024,
        maxRequestSize = 250 * 1024 * 1024
)
public class BoardAjaxController extends HttpServlet {
    private final BoardService boardService = new BoardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();

        try {
            if ("/list".equals(path)) {
                writeJson(response, toJson(boardService.findAll()));
                return;
            }
            if ("/detail".equals(path)) {
                BoardDTO board = boardService.findById(parseInt(request.getParameter("boardId")));
                writeJson(response, toJson(board));
                return;
            }
            if ("/edit".equals(path)) {
                BoardDTO board = boardService.findByIdForEdit(parseInt(request.getParameter("boardId")));
                writeJson(response, toJson(board));
                return;
            }
            if ("/comments".equals(path)) {
                writeJson(response, toJson(boardService.findComments(parseInt(request.getParameter("boardId")))));
                return;
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, resultJson(false, e.getMessage()));
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, resultJson(false, "Board request failed."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();

        try {
            if ("/create".equals(path)) {
                BoardDTO board = toBoard(request, false);
                board.setContent(appendMediaMarkup(board.getContent(), saveUploadedMedia(request)));
                boardService.create(board);
                writeJson(response, resultJson(true, "Board created."));
                return;
            }
            if ("/update".equals(path)) {
                BoardDTO board = toBoard(request, true);
                List<String> media = collectExistingMedia(request);
                media.addAll(saveUploadedMedia(request));
                board.setContent(appendMediaMarkup(board.getContent(), media));
                boardService.update(board);
                writeJson(response, resultJson(true, "Board updated."));
                return;
            }
            if ("/delete".equals(path)) {
                boardService.delete(parseInt(request.getParameter("boardId")), request.getParameter("password"));
                writeJson(response, resultJson(true, "Board deleted."));
                return;
            }
            if ("/comments/create".equals(path)) {
                boardService.createComment(toComment(request));
                writeJson(response, resultJson(true, "Comment created."));
                return;
            }
            if ("/comments/delete".equals(path)) {
                boardService.deleteComment(parseInt(request.getParameter("commentId")), request.getParameter("password"));
                writeJson(response, resultJson(true, "Comment deleted."));
                return;
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, resultJson(false, e.getMessage()));
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, resultJson(false, "Board request failed."));
        }
    }

    private BoardDTO toBoard(HttpServletRequest request, boolean requireId) {
        BoardDTO board = new BoardDTO();
        if (requireId) {
            board.setBoardId(parseInt(request.getParameter("boardId")));
        }
        board.setWriter(request.getParameter("writer"));
        board.setPassword(request.getParameter("password"));
        board.setTitle(request.getParameter("title"));
        board.setContent(request.getParameter("content"));
        return board;
    }

    private BoardDTO toComment(HttpServletRequest request) {
        BoardDTO comment = new BoardDTO();
        comment.setRef(parseInt(request.getParameter("boardId")));
        comment.setWriter(request.getParameter("writer"));
        comment.setPassword(request.getParameter("password"));
        comment.setContent(request.getParameter("content"));
        return comment;
    }

    private List<String> saveUploadedMedia(HttpServletRequest request) throws IOException, ServletException {
        List<String> mediaUrls = new ArrayList<>();
        String contentType = request.getContentType();

        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            return mediaUrls;
        }

        String uploadPath = getServletContext().getRealPath("/uploads/board");
        if (uploadPath == null) {
            throw new IllegalStateException("Upload path is not available.");
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IllegalStateException("Failed to create upload directory.");
        }

        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            if (!"mediaFiles".equals(part.getName()) || part.getSize() == 0) {
                continue;
            }

            String mediaType = resolveMediaType(part.getContentType());
            String originalName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            String extension = extensionOf(originalName);
            String savedName = UUID.randomUUID() + extension;
            File savedFile = new File(uploadDir, savedName);

            part.write(savedFile.getAbsolutePath());
            mediaUrls.add("[[" + mediaType + ":" + request.getContextPath() + "/uploads/board/" + savedName + "]]");
        }

        return mediaUrls;
    }

    private List<String> collectExistingMedia(HttpServletRequest request) {
        List<String> media = new ArrayList<>();
        String[] existingMedia = request.getParameterValues("existingMedia");

        if (existingMedia == null) {
            return media;
        }

        for (String item : existingMedia) {
            if (item != null && item.matches("\\[\\[(image|video):[^]]+]]")) {
                media.add(item);
            }
        }

        return media;
    }

    private String resolveMediaType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("Uploaded file type is missing.");
        }
        if (contentType.startsWith("image/")) {
            return "image";
        }
        if (contentType.startsWith("video/")) {
            return "video";
        }
        throw new IllegalArgumentException("Only image and video files can be uploaded.");
    }

    private String extensionOf(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex).replaceAll("[^A-Za-z0-9.]", "");
    }

    private String appendMediaMarkup(String content, List<String> mediaUrls) {
        if (mediaUrls.isEmpty()) {
            return content;
        }
        StringBuilder builder = new StringBuilder(content == null ? "" : content);
        for (String mediaUrl : mediaUrls) {
            builder.append(System.lineSeparator()).append(mediaUrl);
        }
        return builder.toString();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number.");
        }
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }

    private String toJson(List<BoardDTO> boards) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < boards.size(); i++) {
            if (i > 0) json.append(',');
            json.append(toJson(boards.get(i)));
        }
        return json.append(']').toString();
    }

    private String toJson(BoardDTO board) {
        return "{"
                + "\"boardId\":" + board.getBoardId() + ","
                + "\"writer\":\"" + escapeJson(board.getWriter()) + "\","
                + "\"title\":\"" + escapeJson(board.getTitle()) + "\","
                + "\"content\":\"" + escapeJson(board.getContent()) + "\","
                + "\"readCount\":" + board.getReadCount() + ","
                + "\"ref\":" + board.getRef() + ","
                + "\"reStep\":" + board.getReStep() + ","
                + "\"reLevel\":" + board.getReLevel() + ","
                + "\"commentCount\":" + board.getCommentCount() + ","
                + "\"createdAt\":\"" + escapeJson(board.getCreatedAt()) + "\""
                + "}";
    }

    private String resultJson(boolean success, String message) {
        return "{\"success\":" + success + ",\"message\":\"" + escapeJson(message) + "\"}";
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "\\n");
    }
}
