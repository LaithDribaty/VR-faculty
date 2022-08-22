<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class House extends Model
{
    use HasFactory;

    protected $fillable = [
        'location',
        'description',
        'price',
        'size',
        'user_id'
    ];

    public function user() 
    {
        return $this->belongsTo(User::class);
    }

    public function walls()
    {
        return $this->hasMany(Wall::class);
    }

    public function objects()
    {
        return $this->hasMany(ObjectModel::class);
    }

    public function floors()
    {
        return $this->hasMany(Floor::class);
    }
}
