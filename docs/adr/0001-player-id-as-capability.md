---
status: accepted
---

# Player id is the credential for making a move

There are no user accounts, sessions or logins. A human receives their Player's external id exactly once, in the response that created the Game, claimed its empty seat or handed them their seat in a Rematch, and it is never returned again. A Move is accepted only if the request carries the id of the Player whose turn it is. The Game's own external id is public: anyone who holds it may read the Game, which is how watching works, but cannot move.

## Considered Options

- **Player id as a secret capability** (chosen): no auth stack, nothing to store beyond what already exists, and cheating requires guessing a random UUID.
- **Anonymous server session or cookie per browser**: ties a seat to a browser, which breaks the "share a link" flow and adds state the API otherwise has no use for.
- **User accounts with login**: the dormant `User` entity exists for history and finding opponents later, not for proving who is moving now. Bringing it in here would front-load an auth stack onto a learning project.

## Consequences

- Read responses must never contain a player id. Whose turn it is is reported by Figure.
- The creator's own player id is the only one they get; the opponent's id is handed out by the claim, not by creation.
- If real authentication arrives later, the player id can stay as the seat's identity and the check moves from "does the body carry it" to "does the authenticated User own it".
- A Rematch is a third place a player id is handed out. The old player id is the credential for asking, and for collecting the seat in the next Game; each human collects their own with their own request, so a read only ever says which Figures asked and which Game is next. Once collected, asking again with the old id is refused.
