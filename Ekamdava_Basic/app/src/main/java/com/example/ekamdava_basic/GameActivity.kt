package com.example.ekamdava_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlin.math.log
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var players: MutableList<Player>
    private var currentPlayerIndex = 0
    private var lastkill = false
    private var lastwin = false
    private var change = 74

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val densityDpi = resources.displayMetrics.densityDpi
        if (densityDpi == 440) {
            change = 73
            setContentView(R.layout.activity_game)
        } else {
            change = 74
            setContentView(R.layout.activity_game2)
        }

        val numPlayers = intent.getIntExtra("NUM_PLAYERS", 2)
        removeExcessPieces(numPlayers)

        val diceImageView = findViewById<ImageView>(R.id.diceImageView)
        val rollButton = findViewById<Button>(R.id.rollButton)

        players = mutableListOf(
            Player(
                "Orange",
                listOf(findViewById(R.id.orange1), findViewById(R.id.orange2), findViewById(R.id.orange3), findViewById(R.id.orange4)),
                mutableListOf(Pair(4, 2), Pair(4, 2), Pair(4, 2), Pair(4, 2)),
                false,
                Pair(4,1),
                Pair(3,2),
                false
            ),
            Player(
                "Blue",
                listOf(findViewById(R.id.blue1), findViewById(R.id.blue2), findViewById(R.id.blue3), findViewById(R.id.blue4)),
                mutableListOf(Pair(2, 4), Pair(2, 4), Pair(2, 4), Pair(2, 4)),
                false,
                Pair(3,4),
                Pair(2,3),
                false
            ),
            Player(
                "Yellow",
                listOf(findViewById(R.id.yellow1), findViewById(R.id.yellow2), findViewById(R.id.yellow3), findViewById(R.id.yellow4)),
                mutableListOf(Pair(0, 2), Pair(0, 2), Pair(0, 2), Pair(0, 2)),
                false,
                Pair(0,3),
                Pair(1,2),
                false
            ),
            Player(
                "Green",
                listOf(findViewById(R.id.green1), findViewById(R.id.green2), findViewById(R.id.green3), findViewById(R.id.green4)),
                mutableListOf(Pair(2, 0), Pair(2, 0), Pair(2, 0), Pair(2, 0)),
                false,
                Pair(1,0),
                Pair(2,1),
                false
            )
        )

        rollButton.setOnClickListener {
            lastkill = false
            lastwin = false
            rollButton.isEnabled = false
            val diceroll = rollDice(diceImageView)
            val currentPlayer = players[currentPlayerIndex]
            if(currentPlayer.won==true){
                switchToNextPlayer(numPlayers)
                rollButton.isEnabled = true
            }
            else{
                currentPlayer.pieces.forEachIndexed { index, piece ->
                    piece.bringToFront()
                    if(currentPlayer.positions[index]==Pair(2,2)){
                        piece.isClickable = false
                    }
                    else{
                        piece.isClickable = true
                        piece.setOnClickListener {
//                            Toast.makeText(this, "you clicked piece", Toast.LENGTH_SHORT).show()
                            disableAllPieces()
                            movePiece(piece, index, diceroll,numPlayers)
                            if(currentPlayer.eligibility==true){
                                if(currentPlayer.won==false){
                                    if (players[currentPlayerIndex].positions.all { it == Pair(2, 2) }) {
                                        // If all pieces of this player are in the winning position, add them to the winning set
                                        currentPlayer.won = true
                                    }
                                }
                            }
//                          Toast.makeText(this, "current postiion: ${currentPlayer.positions[index]}",Toast.LENGTH_SHORT).show()
                            if(!(diceroll==4||diceroll==8||lastkill||lastwin)){
                                switchToNextPlayer(numPlayers)
                                when(currentPlayerIndex){
                                    0 -> rollButton.setBackgroundColor(ContextCompat.getColor(this, R.color.Orange))
                                    1 -> rollButton.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue))
                                    2 -> rollButton.setBackgroundColor(ContextCompat.getColor(this, R.color.Yellow))
                                    3 -> rollButton.setBackgroundColor(ContextCompat.getColor(this, R.color.Green))
                                }
                            }
                            rollButton.isEnabled = true
//                            Toast.makeText(this, "Current Player: ${players[currentPlayerIndex].color}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun switchToNextPlayer(numPlayers: Int) {
        // # Check if there is only one remaining player

        // Define the active player indices based on the number of players
        val activeIndices = when (numPlayers) {
            2 -> listOf(0, 2)
            3 -> listOf(0, 1, 2)
            4 -> listOf(0, 1, 2, 3)
            else -> listOf()
        }

        // Check if there is only one remaining player who hasn't won
        val remainingPlayers = activeIndices.filter { !players[it].won }

        if (remainingPlayers.size == 1) {
            val loser = players[remainingPlayers[0]]
            Toast.makeText(this, "Game Over, ${loser.color} Lost the Game", Toast.LENGTH_SHORT).show()
            return
        }

        // # now switching
        if(numPlayers==2){
            if(currentPlayerIndex==0){currentPlayerIndex=2}
            else{currentPlayerIndex=0}
        }
        else {
            currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers
        }

        // Check if the newly selected player has already won
        if (players[currentPlayerIndex].positions.all { it == Pair(2, 2) }) {
            // If all pieces of this player are in the winning position, switch to the next player
            switchToNextPlayer(numPlayers)
            return
        }

//        Toast.makeText(this, "Current Player: ${players[currentPlayerIndex].color}", Toast.LENGTH_SHORT).show()
    }

    private fun movePiece(piece: View, index: Int, steps: Int,numPlayers: Int) {
        val currentPlayer = players[currentPlayerIndex]
        val entryGate = currentPlayer.entryGates
        val winningGate = currentPlayer.winningGates

        for(i in 0 until steps){
            var currentPosition: Pair<Int,Int> = Pair(currentPlayer.positions[index].first, currentPlayer.positions[index].second)
            var row = currentPosition.first
            var col = currentPosition.second

            if(row==0 || col==0 || row==4 || col==4){
                if(currentPosition!=entryGate || currentPlayer.eligibility==false){
                    if (row == 0 && col > 0) {
                        moveLeft(piece, index)
                    } else if (row < 4 && col == 0) {
                        moveDown(piece, index)
                    } else if (row == 4 && col < 4) {
                        moveRight(piece, index)
                    } else if (row > 0 && col == 4) {
                        moveUp(piece, index)
                    }
                }
                else { // to go inside
                    if(row==0 && col==3){ moveDown(piece,index)}
                    else if(row==1 && col==0){ moveRight(piece,index)}
                    else if(row==3 && col==4){ moveLeft(piece,index)}
                    else if(row==4 && col==1){moveUp(piece,index)}
                }
            }
            else{ // inside path
                if(currentPosition!=winningGate){
                    if (row == 1 && col < 3) {
                        moveRight(piece, index)
                    } else if (row > 1 && col == 1) {
                        moveUp(piece, index)
                    } else if (row == 3 && col > 1) {
                        moveLeft(piece, index)
                    } else if (row < 3 && col == 3) {
                        moveDown(piece, index)
                    }
                }
                else{
                    if(steps-i==1){
                        if(row==1 && col==2){ moveDown(piece,index)}
                        else if(row==2 && col==1){ moveRight(piece,index)}
                        else if(row==2 && col==3){ moveLeft(piece,index)}
                        else if(row==3 && col==2){moveUp(piece,index)}
                        lastwin = true
                    }
                    else{
                        if(row==1 && col==2){ moveRight(piece,index)}
                        else if(row==2 && col==1){ moveUp(piece,index)}
                        else if(row==2 && col==3){ moveDown(piece,index)}
                        else if(row==3 && col==2){moveLeft(piece,index)}
                    }
                }
            }
        } // for loop ends here

        // lets check if the possibility to kill a piece exist or not, if exist then kill that piece
        var currentPosition: Pair<Int,Int> = Pair(currentPlayer.positions[index].first, currentPlayer.positions[index].second)
        if(!(currentPosition==Pair(0,2)|| currentPosition==Pair(2,0) || currentPosition==Pair(2,4) || currentPosition==Pair(4,2))) {
            killpiece(currentPlayer,currentPosition,numPlayers)
        }
    }

    private fun killpiece(currentPlayer: Player, currentPosition: Pair<Int,Int>, numPlayers: Int){
        for (i in 0 until 4) {
            if(numPlayers==2){if(i==1 || i==3){continue}}
            if(numPlayers==3){if(i==3){continue}}
            if (i == currentPlayerIndex) {
                continue;
            } else {
                for (j in 0 until 4) {
                    if (currentPosition == players[i].positions[j] && currentPosition!=Pair(2,2)) {
                        var piece = players[i].pieces[j]
                        resetMargins(piece, players[i], j)
                        currentPlayer.eligibility = true
                        lastkill = true
                        break;
                    }
                }
            }
        }
    }

    private fun resetMargins(piece: View, player: Player, index: Int) {
        val layoutParams = piece.layoutParams as ConstraintLayout.LayoutParams
        val density = resources.displayMetrics.density

        if(player.color=="Orange"){
            if(index==0){
                layoutParams.topMargin = 830
                layoutParams.marginStart = 520
            }
            else if(index==1){
                layoutParams.topMargin = 945
                layoutParams.marginStart = 520
            }
            else if(index==2){
                layoutParams.topMargin = 882
                layoutParams.marginStart = 452
            }
            else{//index==3
                layoutParams.topMargin = 882
                layoutParams.marginStart = 588
            }
            player.positions[index]=Pair(4,2)
        }
        else if(player.color=="Green"){
            if(index==0){
                layoutParams.topMargin = 441
                layoutParams.marginStart = 137
            }
            else if(index==1){
                layoutParams.topMargin = 504
                layoutParams.marginStart = 74
            }
            else if(index==2){
                layoutParams.topMargin = 504
                layoutParams.marginStart = 200
            }
            else{//index==3
                layoutParams.topMargin = 557
                layoutParams.marginStart = 137
            }
            player.positions[index]=Pair(2,0)
        }
        else if(player.color=="Yellow"){
            if(index==0){
                layoutParams.topMargin = 53
                layoutParams.marginStart = 520
            }
            else if(index==1){
                layoutParams.topMargin = 105
                layoutParams.marginStart = 452
            }
            else if(index==2){
                layoutParams.topMargin = 105
                layoutParams.marginStart = 588
            }
            else{//index==3
                layoutParams.topMargin = 168
                layoutParams.marginStart = 520
            }
            player.positions[index]=Pair(0,2)
        }
        else{// player.color=="Blue"
            if(index==0){
                layoutParams.topMargin = 504
                layoutParams.marginStart = 851
            }
            else if(index==1){
                layoutParams.topMargin = 504
                layoutParams.marginStart = 966
            }
            else if(index==2){
                layoutParams.topMargin = 557
                layoutParams.marginStart = 914
            }
            else{//index==3
                layoutParams.topMargin = 441
                layoutParams.marginStart = 914
            }
            player.positions[index]=Pair(2,4)
        }

        piece.layoutParams = layoutParams
        piece.requestLayout()

        // For debugging purposes, show a toast message with the new margins
//        Toast.makeText(this, "TopMargin: ${layoutParams.topMargin}, StartMargin: ${layoutParams.marginStart}", Toast.LENGTH_SHORT).show()
    }

    private fun moveUp(piece: View, index: Int) {
        val layoutParams = piece.layoutParams as ConstraintLayout.LayoutParams
        val density = resources.displayMetrics.density
//        val change = 74
        val px = (change * density).toInt()
        layoutParams.topMargin -= px
        piece.layoutParams = layoutParams
        piece.requestLayout()
        val currentPlayer = players[currentPlayerIndex]
        currentPlayer.positions[index] = Pair(currentPlayer.positions[index].first - 1, currentPlayer.positions[index].second)
//        Toast.makeText(this, "Moved Up", Toast.LENGTH_SHORT).show()
    }

    private fun moveDown(piece: View, index: Int) {
        val layoutParams = piece.layoutParams as ConstraintLayout.LayoutParams
        val density = resources.displayMetrics.density
//        val change = 74
        val px = (change * density).toInt()
        layoutParams.topMargin += px
        piece.layoutParams = layoutParams
        piece.requestLayout()

        val currentPlayer = players[currentPlayerIndex]
        currentPlayer.positions[index] = Pair(currentPlayer.positions[index].first +1, currentPlayer.positions[index].second)
//        Toast.makeText(this, "Moved Down", Toast.LENGTH_SHORT).show()
    }

    private fun moveLeft(piece: View, index: Int) {
        val layoutParams = piece.layoutParams as ConstraintLayout.LayoutParams
        val density = resources.displayMetrics.density
//        val change = 74
        val px = (change * density).toInt()
        layoutParams.marginStart -= px
        piece.layoutParams = layoutParams
        piece.requestLayout()
        val currentPlayer = players[currentPlayerIndex]
        currentPlayer.positions[index] = Pair(currentPlayer.positions[index].first, currentPlayer.positions[index].second-1)
//        Toast.makeText(this, "Moved Left", Toast.LENGTH_SHORT).show()
    }

    private fun moveRight(piece: View, index: Int) {
        val layoutParams = piece.layoutParams as ConstraintLayout.LayoutParams
        val density = resources.displayMetrics.density
//        val change = 74
        val px = (change * density).toInt()
        layoutParams.marginStart += px
        piece.layoutParams = layoutParams
        piece.requestLayout()
        val currentPlayer = players[currentPlayerIndex]
        currentPlayer.positions[index] = Pair(currentPlayer.positions[index].first , currentPlayer.positions[index].second+1)
//        Toast.makeText(this, "Moved Right", Toast.LENGTH_SHORT).show()
    }

    private fun disableAllPieces() {
        players.forEach { player ->
            player.pieces.forEach { piece ->
                piece.isClickable = false
            }
        }
    }


    private fun removeExcessPieces(numPlayers: Int) {
        val totalPieces = 16 // Total number of pieces on the board

        // Calculate the number of excess pieces to remove

        // excessPieces = totalPieces - currentPieces
        val excessPieces = totalPieces - (numPlayers * 4)

        // List of ImageView IDs representing the excess pieces
        val excessPieceIds = listOf(
            R.id.green1, R.id.green2, R.id.green3, R.id.green4,
            R.id.blue1, R.id.blue2, R.id.blue3, R.id.blue4
        )

        // Hide or remove excess pieces
        for (i in 0 until excessPieces) {
            val excessPieceId = excessPieceIds.getOrNull(i)
            excessPieceId?.let { findViewById<ImageView>(it).visibility = View.GONE}
        }
    }

    private fun rollDice(diceImageView: ImageView): Int {
        val randomNumber = Random.nextInt(1, 6) // Generate random number between 1 and 5 (5 means 8 here)
        val drawableId = when (randomNumber) {
            1 -> R.drawable.ekamdava_dice_1
            2 -> R.drawable.ekamdava_dice_2
            3 -> R.drawable.ekamdava_dice_3
            4 -> R.drawable.ekamdava_dice_4
            else -> R.drawable.ekamdava_dice_8
        }
        diceImageView.setImageResource(drawableId)
        val number = if (randomNumber < 5) randomNumber else 8
        Toast.makeText(this, "You rolled: $number", Toast.LENGTH_SHORT).show()
        return number
    }

    data class Player(
        val color: String,
        val pieces: List<ImageView>,
        val positions: MutableList<Pair<Int, Int>>, // Positions in a 5x5 matrix
        var eligibility: Boolean, // Eligibility to enter the inner maze
        val entryGates: Pair<Int, Int>, // Position of entry gates
        val winningGates: Pair<Int, Int>, // Position of winning gates
        var won: Boolean
    )
//    asdduhasfdh

}