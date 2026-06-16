package kr.or.bit.dao;

import kr.or.bit.dto.BoardDTO;
import kr.or.bit.utils.ConnectionHelper;
import kr.or.bit.utils.DBType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {
    public List<BoardDTO> findAll() {
        List<BoardDTO> boards = new ArrayList<>();
        String sql = "SELECT b.board_id, b.writer, b.title, b.content, b.read_count, "
                + "b.ref, b.re_step, b.re_level, b.is_deleted, "
                + "TO_CHAR(b.created_at, 'YYYY-MM-DD HH24:MI') created_at, "
                + "COUNT(c.comment_id) comment_count "
                + "FROM jspboard b "
                + "LEFT JOIN reply c ON c.board_id = b.board_id "
                + "WHERE b.re_level = 0 AND b.is_deleted = 'N' "
                + "GROUP BY b.board_id, b.writer, b.title, b.content, b.read_count, "
                + "b.ref, b.re_step, b.re_level, b.is_deleted, b.created_at "
                + "ORDER BY b.board_id DESC";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                boards.add(mapRow(rs, false));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to select boards.", e);
        }
        return boards;
    }

    public BoardDTO findById(int boardId) {
        String sql = "SELECT board_id, writer, title, content, read_count, ref, re_step, re_level, is_deleted, "
                + "TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI') created_at, 0 comment_count "
                + "FROM jspboard WHERE board_id = ? AND re_level = 0 AND is_deleted = 'N'";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, boardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs, true);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to select board.", e);
        }
        return null;
    }

    public List<BoardDTO> findComments(int boardId) {
        List<BoardDTO> comments = new ArrayList<>();
        String sql = "SELECT comment_id board_id, board_id ref, writer, 'COMMENT' title, content, 0 read_count, "
                + "1 re_step, 1 re_level, 'N' is_deleted, "
                + "TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI') created_at, 0 comment_count "
                + "FROM reply "
                + "WHERE board_id = ? "
                + "ORDER BY comment_id ASC";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, boardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapRow(rs, true));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to select comments.", e);
        }
        return comments;
    }

    public int insert(BoardDTO board) {
        String sql = "INSERT INTO jspboard "
                + "(board_id, writer, password, title, content, read_count, ref, re_step, re_level, is_deleted) "
                + "VALUES (seq_jspboard_id.NEXTVAL, ?, ?, ?, ?, 0, seq_jspboard_id.CURRVAL, 0, 0, 'N')";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, board.getWriter());
            pstmt.setString(2, board.getPassword());
            pstmt.setString(3, board.getTitle());
            pstmt.setString(4, board.getContent());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert board.", e);
        }
    }

    public int insertComment(BoardDTO comment) {
        String sql = "INSERT INTO reply "
                + "(comment_id, board_id, writer, password, content) "
                + "VALUES (seq_reply_id.NEXTVAL, ?, ?, ?, ?)";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, comment.getRef());
            pstmt.setString(2, comment.getWriter());
            pstmt.setString(3, comment.getPassword());
            pstmt.setString(4, comment.getContent());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert comment.", e);
        }
    }

    public int update(BoardDTO board) {
        String sql = "UPDATE jspboard SET writer = ?, title = ?, content = ?, updated_at = SYSDATE "
                + "WHERE board_id = ? AND password = ? AND re_level = 0 AND is_deleted = 'N'";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, board.getWriter());
            pstmt.setString(2, board.getTitle());
            pstmt.setString(3, board.getContent());
            pstmt.setInt(4, board.getBoardId());
            pstmt.setString(5, board.getPassword());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update board.", e);
        }
    }

    public int delete(int boardId, String password) {
        String deleteRepliesSql = "DELETE FROM reply WHERE board_id = ?";
        String deleteBoardSql = "UPDATE jspboard SET is_deleted = 'Y', updated_at = SYSDATE "
                + "WHERE board_id = ? AND password = ? AND re_level = 0 AND is_deleted = 'N'";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE)) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement replyStatement = conn.prepareStatement(deleteRepliesSql);
                    PreparedStatement boardStatement = conn.prepareStatement(deleteBoardSql)
            ) {
                replyStatement.setInt(1, boardId);
                replyStatement.executeUpdate();

                boardStatement.setInt(1, boardId);
                boardStatement.setString(2, password);
                int affectedRows = boardStatement.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    return 0;
                }

                conn.commit();
                return affectedRows;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete board.", e);
        }
    }

    public int deleteComment(int commentId, String password) {
        String sql = "DELETE FROM reply WHERE comment_id = ? AND password = ?";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commentId);
            pstmt.setString(2, password);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete comment.", e);
        }
    }

    public void increaseReadCount(int boardId) {
        String sql = "UPDATE jspboard SET read_count = read_count + 1 "
                + "WHERE board_id = ? AND re_level = 0 AND is_deleted = 'N'";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, boardId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to increase read count.", e);
        }
    }

    private BoardDTO mapRow(ResultSet rs, boolean includeContent) throws SQLException {
        BoardDTO board = new BoardDTO();
        board.setBoardId(rs.getInt("board_id"));
        board.setWriter(rs.getString("writer"));
        board.setTitle(rs.getString("title"));
        board.setContent(includeContent ? rs.getString("content") : "");
        board.setReadCount(rs.getInt("read_count"));
        board.setRef(rs.getInt("ref"));
        board.setReStep(rs.getInt("re_step"));
        board.setReLevel(rs.getInt("re_level"));
        board.setIsDeleted(rs.getString("is_deleted"));
        board.setCreatedAt(rs.getString("created_at"));
        board.setCommentCount(rs.getInt("comment_count"));
        return board;
    }
}
