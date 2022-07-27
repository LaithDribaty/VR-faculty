<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Wall extends Model
{
    use HasFactory;

    protected $fillable = [
        'start',
        'end',
        'house_id',
        'thick'
    ];

    public function house() 
    {
        return $this->belongsTo(House::class);
    }
}
