// Base API URL - adjust if your backend is on a different port
const API_URL = 'http://localhost:8080/player';

// DOM elements
const playerForm = document.getElementById('playerForm');
const playersTable = document.getElementById('playersTable').querySelector('tbody');
const cancelBtn = document.getElementById('cancelBtn');

// Form state
let isEditing = false;

let currentBracket = null;


// Event listeners
document.addEventListener('DOMContentLoaded', () => {
    fetchPlayers();
    fetchAndDisplayBracket();
});
playerForm.addEventListener('submit', handleFormSubmit);
cancelBtn.addEventListener('click', resetForm);

document.getElementById('initBracketBtn').addEventListener('click', initializeBracket);

// Fetch all players
async function fetchPlayers() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            throw new Error('Failed to fetch players');
        }
        const players = await response.json();
        renderPlayers(players);
    } catch (error) {
        console.error('Error fetching players:', error);
        alert('Error fetching players. Check console for details.');
    }
}

// Render players in the table
function renderPlayers(players) {
    playersTable.innerHTML = '';
    players.forEach(player => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${player.id}</td>
            <td>${player.nickname}</td>
            <td>${player.rating}</td>
            <td>
                <button onclick="editPlayer(${player.id})">Edit</button>
                <button onclick="deletePlayer(${player.id})">Delete</button>
            </td>
        `;
        playersTable.appendChild(row);
    });
}

// Handle form submission
async function handleFormSubmit(e) {
    e.preventDefault();

    const playerId = document.getElementById('playerId').value;
    const nickname = document.getElementById('nickname').value;
    const rating = document.getElementById('rating').value;

    const playerData = {
        nickname: nickname,
        rating: parseInt(rating) || 0
    };

    try {
        let response;
        if (isEditing) {
            response = await fetch(`${API_URL}/${playerId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(playerData)
            });
        } else {
            response = await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(playerData)
            });
        }

        if (!response.ok) {
            const errorData = await response.json();
            displayValidationErrors(errorData);
            throw new Error('Request failed');
        }

        resetForm();
        fetchPlayers();
    } catch (error) {
        console.error('Error saving player:', error);
        if (!error.message.includes('Request failed')) {
            alert('Error saving player. Check console for details.');
        }
    }
}

// Display validation errors
function displayValidationErrors(errorData) {
    // Clear previous errors
    document.getElementById('nicknameError').textContent = '';
    document.getElementById('ratingError').textContent = '';

    if (errorData.errors) {
        errorData.errors.forEach(error => {
            if (error.field === 'nickname') {
                document.getElementById('nicknameError').textContent = error.message;
            } else if (error.field === 'rating') {
                document.getElementById('ratingError').textContent = error.message;
            }
        });
    }
}

// Edit player
async function editPlayer(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        if (!response.ok) {
            throw new Error('Failed to fetch player');
        }
        const player = await response.json();

        document.getElementById('playerId').value = player.id;
        document.getElementById('nickname').value = player.nickname;
        document.getElementById('rating').value = player.rating;

        isEditing = true;
        playerForm.querySelector('button[type="submit"]').textContent = 'Update';
    } catch (error) {
        console.error('Error fetching player:', error);
        alert('Error fetching player. Check console for details.');
    }
}

// Delete player
async function deletePlayer(id) {
    if (!confirm('Are you sure you want to delete this player?')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Failed to delete player');
        }

        fetchPlayers();
    } catch (error) {
        console.error('Error deleting player:', error);
        alert('Error deleting player. Check console for details.');
    }
}

// Reset form
function resetForm() {
    playerForm.reset();
    document.getElementById('playerId').value = '';
    isEditing = false;
    playerForm.querySelector('button[type="submit"]').textContent = 'Save';

    // Clear errors
    document.getElementById('nicknameError').textContent = '';
    document.getElementById('ratingError').textContent = '';
}

async function initializeBracket() {
    const assignmentType = document.getElementById('assignmentType').value;

    try {
        const response = await fetch('/bracket', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                assignerType: assignmentType
            })
        });

        if (response.ok) {
            await fetchAndDisplayBracket();
        } else {
            console.error('Failed to initialize bracket');
        }
    } catch (error) {
        console.error('Error initializing bracket:', error);
    }
}

async function fetchAndDisplayBracket() {
    try {
        const response = await fetch('/bracket');
        if (response.ok) {
            const bracketData = await response.json();
            currentBracket = bracketData.bracket;
            renderBracket(currentBracket);
        } else {
            console.error('Failed to fetch bracket');
        }
    } catch (error) {
        console.error('Error fetching bracket:', error);
    }
}

function renderBracket(bracket) {
    const bracketContainer = document.getElementById('bracketVisualization');
    bracketContainer.innerHTML = '';

    if (!bracket || !bracket.rootMatch) {
        bracketContainer.textContent = 'No bracket initialized or no matches available.';
        return;
    }

    // Calculate the depth of the bracket to determine how many rounds we have
    const depth = calculateBracketDepth(bracket.rootMatch);

    // Create container for all rounds
    const bracketLevels = document.createElement('div');
    bracketLevels.className = 'bracket-levels';

    // Create round headers first
    const roundHeaders = document.createElement('div');
    roundHeaders.className = 'round-headers';
    roundHeaders.style.display = 'flex';
    roundHeaders.style.justifyContent = 'center';

    // Generate round names based on depth
    const roundNames = generateRoundNames(depth);

    // Create each round header
    for (let i = 0; i < depth; i++) {
        const header = document.createElement('div');
        header.className = 'round-header';
        header.textContent = roundNames[i];
        header.style.width = '200px'; // Match the width of your match boxes
        header.style.margin = '0 20px';
        roundHeaders.appendChild(header);
    }

    bracketContainer.appendChild(roundHeaders);

    // Create each round container
    for (let i = 0; i < depth; i++) {
        const round = document.createElement('div');
        round.className = 'round';
        round.dataset.round = i + 1;
        bracketLevels.appendChild(round);
    }

    bracketContainer.appendChild(bracketLevels);

    // Recursively render matches starting from the root
    renderMatch(bracket.rootMatch, depth, 0, bracketLevels);
}

function generateRoundNames(depth) {
    const roundNames = [];
    const nameMap = {
        1: 'Final',
        2: 'Semifinals',
        3: 'Quarterfinals',
        4: 'Round of 16',
        5: 'Round of 32'
    };

    // Generate names from finals back to early rounds
    for (let i = depth; i > 0; i--) {
        if (nameMap[i]) {
            roundNames.push(nameMap[i]);
        } else {
            roundNames.push(`Round ${depth - i + 1}`);
        }
    }

    // Reverse to show early rounds first
    return roundNames.reverse();
}

function calculateBracketDepth(match) {
    if (!match) return 0;
    return 1 + Math.max(
        calculateBracketDepth(match.leftMatch),
        calculateBracketDepth(match.rightMatch)
    );
}

function renderMatch(match, totalDepth, currentDepth, bracketLevels) {
    if (!match) return;

    const round = bracketLevels.children[currentDepth];

    const matchElement = document.createElement('div');
    matchElement.className = 'match';
    matchElement.dataset.matchId = match.referenceValue;

    if (currentDepth === totalDepth - 1) {
        matchElement.classList.add('final-match');
    }

    // Player 1
    const player1Div = document.createElement('div');
    player1Div.className = 'match-player';
    player1Div.dataset.playerId = match.player1?.id || '';
    player1Div.textContent = match.player1 ? `${match.player1.nickname} (${match.player1.rating})` : 'TBD';

    if (match.winner && match.winner.id === match.player1?.id) {
        player1Div.classList.add('selected');
    }

    // Player 2 (if exists)
    const player2Div = document.createElement('div');
    player2Div.className = 'match-player';
    player2Div.dataset.playerId = match.player2?.id || '';
    player2Div.textContent = match.player2 ? `${match.player2.nickname} (${match.player2.rating})` : 'TBD';

    if (match.winner && match.winner.id === match.player2?.id) {
        player2Div.classList.add('selected');
    }

    // Add click handlers for selecting winners
    player1Div.addEventListener('click', () => selectWinner(match.referenceValue, match.player1?.id));
    player2Div.addEventListener('click', () => selectWinner(match.referenceValue, match.player2?.id));

    matchElement.appendChild(player1Div);
    matchElement.appendChild(player2Div);

    round.appendChild(matchElement);

    // Recursively render child matches
    renderMatch(match.leftMatch, totalDepth, currentDepth + 1, bracketLevels);
    renderMatch(match.rightMatch, totalDepth, currentDepth + 1, bracketLevels);
}

async function selectWinner(matchReferenceValue, playerId) {
    if (!playerId) return; // Skip if player is TBD

    try {
        const response = await fetch('/match/winner', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                matchReferenceValue: matchReferenceValue,
                playerId: playerId
            })
        });

        if (response.ok) {
            await fetchAndDisplayBracket();
        } else {
            console.error('Failed to select winner');
        }
    } catch (error) {
        console.error('Error selecting winner:', error);
    }
}

// Make functions available globally for button clicks
window.editPlayer = editPlayer;
window.deletePlayer = deletePlayer;