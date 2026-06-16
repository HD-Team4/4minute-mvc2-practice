package kr.or.bit.service;

import kr.or.bit.dao.BoardDAO;
import kr.or.bit.dto.BoardDTO;

import java.util.List;

public class BoardService {
    private final BoardDAO boardDAO = new BoardDAO();

    public List<BoardDTO> findAll() {
        return boardDAO.findAll();
    }

    public BoardDTO findById(int boardId) {
        validateId(boardId);
        boardDAO.increaseReadCount(boardId);
        BoardDTO board = boardDAO.findById(boardId);
        if (board == null) {
            throw new IllegalArgumentException("Board not found.");
        }
        return board;
    }

    public BoardDTO findByIdForEdit(int boardId) {
        validateId(boardId);
        BoardDTO board = boardDAO.findById(boardId);
        if (board == null) {
            throw new IllegalArgumentException("Board not found.");
        }
        return board;
    }

    public List<BoardDTO> findComments(int boardId) {
        validateId(boardId);
        return boardDAO.findComments(boardId);
    }

    public void create(BoardDTO board) {
        validate(board, false);
        boardDAO.insert(board);
    }

    public void createComment(BoardDTO comment) {
        validateId(comment.getRef());
        validateComment(comment);
        if (boardDAO.findById(comment.getRef()) == null) {
            throw new IllegalArgumentException("Board not found.");
        }
        boardDAO.insertComment(comment);
    }

    public void update(BoardDTO board) {
        validate(board, true);
        if (boardDAO.update(board) == 0) {
            throw new IllegalArgumentException("Board not found or password mismatch.");
        }
    }

    public void delete(int boardId, String password) {
        validateId(boardId);
        validatePassword(password);
        if (boardDAO.delete(boardId, password) == 0) {
            throw new IllegalArgumentException("Board not found or password mismatch.");
        }
    }

    public void deleteComment(int commentId, String password) {
        validateId(commentId);
        validatePassword(password);
        if (boardDAO.deleteComment(commentId, password) == 0) {
            throw new IllegalArgumentException("Comment not found or password mismatch.");
        }
    }

    private void validate(BoardDTO board, boolean requireId) {
        if (requireId) {
            validateId(board.getBoardId());
        }
        if (board.getWriter() == null || board.getWriter().isBlank()) {
            throw new IllegalArgumentException("Writer is required.");
        }
        validatePassword(board.getPassword());
        if (board.getTitle() == null || board.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (board.getContent() == null || board.getContent().isBlank()) {
            throw new IllegalArgumentException("Content is required.");
        }
    }

    private void validateComment(BoardDTO comment) {
        if (comment.getWriter() == null || comment.getWriter().isBlank()) {
            throw new IllegalArgumentException("Writer is required.");
        }
        validatePassword(comment.getPassword());
        if (comment.getContent() == null || comment.getContent().isBlank()) {
            throw new IllegalArgumentException("Content is required.");
        }
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid board id.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }
    }
}
