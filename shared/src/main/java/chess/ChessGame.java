package chess;

import chess.rules.EndGameEnforcer;
import chess.rules.Enforcer;

import java.util.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard currentBoard;
    private final Enforcer enforcer;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.enforcer = new EndGameEnforcer();
        this.currentBoard = new ChessBoard();
        this.currentBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(currentBoard, chessGame.currentBoard) && Objects.equals(enforcer, chessGame.enforcer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), currentBoard, enforcer);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // A move is valid if it is a "piece move" for the piece at the input location
        // and making that move would not leave the team’s king in danger of check
        System.out.println("[DEBUG] Getting valid moves");

        final ChessPiece checkPiece = currentBoard.getPiece(startPosition);
        Collection<ChessMove> valid = null;
        if (checkPiece != null) {
            valid = new HashSet<>();
            final Collection<ChessMove> moves = checkPiece.pieceMoves(currentBoard, startPosition);
            // for each move in this piece's move
            for (ChessMove m : moves) {
                System.out.println("Checking move: " + m.toString());

                final ChessBoard hypothetical = new ChessBoard(currentBoard);
                hypothetical.addPiece(m.getStartPosition(), null);
                hypothetical.addPiece(m.getEndPosition(), checkPiece);
                // if the move does NOT result in check
                if (!enforcer.isInCheck(checkPiece.getTeamColor(), hypothetical)) {
                    valid.add(m);
                }
            }
        }
        return valid;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        System.out.println("[DEBUG] makeMove: " + move.toString());
        System.out.println(currentBoard.toString());

        final Collection<ChessMove> valid = validMoves(move.getStartPosition());
        final ChessPiece selected = currentBoard.getPiece(move.getStartPosition());
        if (valid != null && !valid.contains(move) && selected.getTeamColor() == getTeamTurn()) {
            // set the end space to selected or promotion piece, if it's not null
            final ChessPiece.PieceType type = move.getPromotionPiece();
            currentBoard.addPiece(
                    move.getEndPosition(),
                    (type != null) ?
                            new ChessPiece(selected.getTeamColor(), type) :
                            selected
            );
            currentBoard.addPiece(move.getStartPosition(), null);
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return enforcer.isInCheck(teamColor, this.currentBoard);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return enforcer.isInCheckmate(teamColor, this.currentBoard);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return enforcer.isInStalemate(teamColor, this.currentBoard);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }
}
