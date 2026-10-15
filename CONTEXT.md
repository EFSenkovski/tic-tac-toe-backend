# Tic-Tac-Toe

A learn-by-doing web API for playing tic-tac-toe. A human plays against another human or against the machine at one of three difficulties, or watches two machines play each other.

## Language

**Game**:
One match of tic-tac-toe between two Players on one Board. Identified by an external id that is safe to share.
_Avoid_: Match, session, room

**Board**:
The nine cells of a Game and the Figures placed on them.
_Avoid_: Grid

**Cell**:
One of the nine positions on a Board, addressed by an index from 0 to 8.
_Avoid_: Square, slot, coordinate

**Figure**:
The symbol a Player places on the Board: X or O. Every Game has one X Player and one O Player. The person creating a Game chooses which Figure they take.
_Avoid_: Mark, symbol, piece

**Move**:
A Player placing their Figure on an empty Cell. Only the current Player may move.
_Avoid_: Turn (a turn is whose go it is; a move is the act)

**Game Status**:
The outcome the Board currently shows: X wins, O wins, draw, or not finished. Says nothing about whether the Game can be joined or is still being played; that is the Phase.
_Avoid_: Result, outcome, state

**Phase**:
Where a Game is in its life: waiting (a human seat is still empty), playing (Moves are accepted), or over (no more Moves). Distinct from Game Status, which says who won.
_Avoid_: State, stage, lifecycle

**Player**:
One of the two seats in a single Game, one per Figure. Both exist from the moment the Game is created. A seat is occupied by the machine, by a human, or is empty until a human Claims it. A Player belongs to exactly one Game and may optionally be linked to a User.
_Avoid_: Participant, opponent, side

**Claim**:
A human taking the empty seat of a waiting Game by following its link. Only possible while the Game is waiting; once claimed, the Game is playing.
_Avoid_: Join, invite, accept

**Rematch**:
A Player of a finished Game asking for another Game with the same Player Types and every Figure swapped, so who opens alternates. The machine always agrees, so against it the next Game starts at once; between two humans it exists only once both have asked. The finished Game then points to its next Game, and each human is handed their seat in it by their own request.
_Avoid_: Play again, replay, new round

**Player Type**:
Who occupies a seat: a human ("user"), or the machine at one of three difficulties ("easy", "medium", "hard"). One concept on purpose; not split into kind and difficulty.
_Avoid_: Level, difficulty, role

**Advance**:
Asking the machine to make its next Move in a Game where the machine is to move and no human is waiting on it, which only happens when both seats are machines. Anyone with the Game's id may do it.
_Avoid_: Tick, step, auto-move

**Move Strategy**:
How the machine picks its next Move for a given Player Type. Easy picks a random empty Cell; medium takes a win or blocks one, otherwise random; hard plays perfectly.
_Avoid_: AI, bot, engine

**User**:
An account that outlives any single Game, identified by email. Exists so that a person can later have a history of Games and find other Users to play with. A human Player does not need a User.
_Avoid_: Account, member, profile
