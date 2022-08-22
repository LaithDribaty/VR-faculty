<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Floor extends Model
{
    use HasFactory;


    protected $fillable = [
        'p1',
        'p2',
        'p3',
        'p4',
        'house_id',
        'image_url'
    ];

    public function house()
    {
        return $this->belongsTo(House::class);
    }
}
